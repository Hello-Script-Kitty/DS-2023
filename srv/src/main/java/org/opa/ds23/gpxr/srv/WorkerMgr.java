package org.opa.ds23.gpxr.srv;

import org.opa.ds23.gpxr.common.data.Activity;
import org.opa.ds23.gpxr.common.data.ActivityChunk;
import org.opa.ds23.gpxr.common.data.DataUtils;
import org.opa.ds23.gpxr.common.data.ReductionResult;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;
import org.opa.ds23.gpxr.utilities.Exceptions;
import org.opa.ds23.gpxr.utilities.LockingContainer;
import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

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
  private final Queue<WorkerHandler> _workers = new LinkedList<>();
  //hold result futures
  private final Map<String, LockingContainer<ReductionResult>> _results = new HashMap<>();

  /**
   * Initialize with the TCP port
   *
   * @param port Worker manager control port
   */
  WorkerMgr(int port) throws IOException {
    _pl = new ArrayList<>();
    //launch control service
    _srv = new ServerSocket(port);
    _srv.setReuseAddress(true);
    _listener = new Thread(new Srv());
    _listener.start();
  }

  /**
   * Submit a list of workloads to the workers
   *
   * @param act An activity to split among the workers
   * @return A list of Futures for the results
   */
  synchronized LockingContainer<ReductionResult>[] submitReductions(Activity act) throws IOException,
    InterruptedException {
    List<ActivityChunk> workloads = DataUtils.toChunks(act, _workers.size());
    List<LockingContainer<ReductionResult>> fl = new ArrayList<>(workloads.size());
    logger.debug("Activity " + act.id + " split into " + workloads.size() + " chunks");
    for (ActivityChunk chunk : workloads) {
//      logger.debug("Submitting workload");
      fl.add(submitReduction(chunk));
    }
//    logger.debug("Done submitting workloads");
    return fl.toArray(new LockingContainer[0]);
  }

  /**
   * Submit a single workload to a worker
   *
   * @param workload The workload
   * @return A Future for the result
   */
  synchronized LockingContainer<ReductionResult> submitReduction(ActivityChunk workload) throws IOException,
    InterruptedException {
    LockingContainer<ReductionResult> f = new LockingContainer<>();
    send(workload);
    _results.put(workload.chunkId, f);
    return f;
  }

  /**
   * Round-robin submission to workers
   *
   * @param workload
   */
  private void send(ActivityChunk workload) throws IOException, InterruptedException {
    //get the next worker
    WorkerHandler w = _workers.remove();
    //send workload
    logger.debug("Sending workload " + workload.chunkId + "/" + workload.activityId + "to worker " + w.id);
    w.send(workload);
    //put worker at the end of the queue
    _workers.offer(w);
  }

  private synchronized void handleChunk(ReductionResult chunk) {
    logger.debug("Received result for " + chunk.chunkId + "/" + chunk.activityId);
    LockingContainer<ReductionResult> f = _results.remove(chunk.chunkId);
    if (f != null) {
      f.set(chunk);
    }
    else
      logger.error("ReductionChuck received for unknown chunk");
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
          synchronized (_workers) {
            //spawn handling mechanism
            WorkerHandler ch = new WorkerHandler(c);
            _workers.add(ch); //keep worker
            logger.debug("New worker enrolled. Workers: " + _workers.size());
          }
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
      Thread t = new Thread(con);
      t.start();
    }

    private void closeHandler() {
      synchronized (_workers) {
        //remove self from available workers
        _workers.remove(this);
        logger.debug("Worker lost. Workers: " + _workers.size());
      }
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
