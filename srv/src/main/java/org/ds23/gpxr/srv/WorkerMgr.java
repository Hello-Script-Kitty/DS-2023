package org.ds23.gpxr.srv;

import org.ds23.gpxr.utilities.LogManager;
import org.ds23.gpxr.utilities.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

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
  private int _port;
  private ServerSocket _srv;

  /**
   * Initialize with the control port
   *
   * @param port Worker manager control port
   */
  WorkerMgr(int port) throws IOException {
    _pl = new ArrayList<>();
    _port = port;
    //launch control server
    _srv = new ServerSocket(_port);
    _srv.setReuseAddress(true);
    _listener = new Thread(new Srv());
    _listener.run();
  }

  /**
   * Submit a list of workloads to the workers
   *
   * @param workloads A list of workloads to submit
   * @return A list of Futures for the results
   */
  List<Future<RedRslt>> submitReductions(List<RedWork> workloads) {
    //FIXME Implement
  }

  /**
   * Submit a single workload to a worker
   *
   * @param workload The workload
   * @return A Future for the result
   */
  Future<RedRslt> submitReduction(RedWork workload) {
    //FIXME Implement
  }

  /**
   * Worker controller
   * <p>
   * A controller will send workloads to a worker and listen for responses
   */
  private class Worker implements Runnable {
    private InetSocketAddress addr; //worker control address/port
    private Map<String, RedWork> workloads; //workloads assigned to worker (currently working)

    public Worker(InetSocketAddress addr) {
      this.addr = addr;
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
          //FIXME spawn thread for worker subscription
        } catch (IOException e) {
          logger.error("Failed to accept connection");
        }
      }
    }
  }

  /**
   * Handle worker subscription message
   */
  private class ClientHandler implements Runnable {
    Socket c;

    public ClientHandler(Socket client) {
      c = client;
    }

    @Override
    public void run() {
      //FIXME handle worker subscription message
    }
  }
}
