package org.opa.ds23.gpxr.srv;

import org.opa.ds23.gpxr.common.data.Activity;
import org.opa.ds23.gpxr.common.data.ActivityChunk;
import org.opa.ds23.gpxr.common.data.DataUtils;
import org.opa.ds23.gpxr.common.data.ReductionResult;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;
import org.opa.ds23.gpxr.utilities.Exceptions;
import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Worker Manager
 * <p>
 * Responsible for managing workers. Opens a port for workers to connect and pools connected workers.
 * <p>
 * Each worker gets its own service port for sending back workload results
 */
public class WorkerMgr {
  private static final Logger logger = LogManager.getLogger(WorkerMgr.class);
  private final Thread _listener;

  private List<Worker> _pl;
  private ServerSocket _srv;

  //hold available workers
  private final BlockingQueue<WorkerHandler> _workers = new LinkedBlockingQueue<>();
  //hold result futures
  private final ConcurrentMap<String, CompletableFuture<ReductionResult>> _results = new ConcurrentHashMap<>();

  /**
   * Initialize with the comm port
   *
   * @param port Worker manager control port
   */
  WorkerMgr(int port) throws IOException {
    _pl = new ArrayList<>();
    //launch control service
    _srv = new ServerSocket(port);
    _srv.setReuseAddress(true);
    _listener = new Thread(new Srv());
    Ctx.es.execute(_listener);
  }

  /**
   * Submit a list of workloads to the workers
   *
   * @param workloads A list of workloads to submit
   * @return A list of Futures for the results
   */
  synchronized CompletableFuture<ReductionResult>[] submitReductions(List<ActivityChunk> workloads) {
    List<CompletableFuture<ReductionResult>> fl = new ArrayList<>(workloads.size());
    for (ActivityChunk chunk : workloads) {
      fl.add(submitReduction(chunk));
    }
    return fl.toArray(new CompletableFuture[0]);
  }

  /**
   * Submit a single workload to a worker
   *
   * @param workload The workload
   * @return A Future for the result
   */
  synchronized CompletableFuture<ReductionResult> submitReduction(ActivityChunk workload) {
    CompletableFuture<ReductionResult> f = new CompletableFuture<>();
    _results.put(workload.chunkId, f);
    return f;
  }

  /**
   * Round-robin submission to workers
   *
   * @param workload
   */
  private void send(ActivityChunk workload) throws IOException, InterruptedException {
    //get next worker
    WorkerHandler w = _workers.remove();
    //send workload
    w.send(workload);
    //put worker at the end of the queue
    _workers.offer(w);
  }

  private synchronized void handleChunk(ReductionResult chunk) {
    CompletableFuture<ReductionResult> f = _results.remove(chunk.chunkId);
    if (f != null)
      if (f.isCancelled() || f.isCompletedExceptionally())
        logger.error("ReductionChuck received for canceled or excepted future");
      else
        f.complete(chunk);
    else
      logger.error("ReductionChuck received for unknown chunk");
  }

  public int size() {
    return _workers.size();
  }

  /**
   * Worker controller
   * <p>
   * A controller handles communications with a particular worker, sends workloads to a worker and listens for
   * responses.
   */
  private class Worker implements Runnable {
    private Socket socket; //worker control address/port
    private Map<String, ActivityChunk> workloads; //workloads assigned to worker (currently working)

    public Worker(Socket socket) {
      this.socket = socket;
    }

    @Override
    public void run() {
      //FIXME start result service
    }
  }

  /**
   * Runs a service socket to accept worker enrollment
   */
  private class Srv implements Runnable {

    @Override
    public void run() {
      while (true) {
        try {
          Socket c = _srv.accept();
          //spawn handling mechanism
          WorkerHandler ch = new WorkerHandler(c);
          _workers.add(ch); //keep worker
//          Connection con = new Connection(c, ch::messageHandler);
//          execSrv.submit(con);
        } catch (IOException e) {
          logger.error("Failed to accept connection");
        }
      }
    }
  }

  /**
   * Handles worker communications
   */
  private class WorkerHandler {
    final Connection con;
    final String id = UUID.randomUUID().toString();

    public WorkerHandler(Socket c) {
      con = new Connection(c, this::messageHandler, this::closeHandler);
      Ctx.es.execute(con);
    }

    private void closeHandler() {
      //remove self from available workers
      _workers.remove(this);
    }

    /**
     * Handles incoming message from worker
     *
     * @param msg The incoming message
     */
    public void messageHandler(byte[] msg) {
      try {
        Message m = Message.deserialize(msg);
        if (Objects.requireNonNull(m.type) == Type.ReductionResult) {
          handleChunk(ReductionResult.deserialize(m.data));
        } else {
          logger.debug("Unhandled worker message type : " + m.type);
        }
      } catch (IOException | ClassNotFoundException e) {
        logger.error("Error deserializing message. Ignoring message.");
        logger.error(Exceptions.getStackTrace(e));
      }
    }

    /**
     * Send {@link ActivityChunk} to Worker
     *
     * @param workload The {@link ActivityChunk} to send
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized void send(ActivityChunk workload) throws IOException, InterruptedException {
      Message m = new Message(Type.Workload, workload);
      con.send(m.serialize());
    }
  }
}
