package org.opa.ds23.gpxr.worker;

import org.opa.ds23.gpxr.common.data.ActivityChunk;
import org.opa.ds23.gpxr.common.data.ReductionResult;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;
import org.opa.ds23.gpxr.utilities.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Main Worker Class (worker app entry point)
 */
public class Worker {
  private static final Logger logger = LogManager.getLogger(Worker.class);
  private final ThreadPool pool = new ThreadPool(Runtime.getRuntime().availableProcessors());
  Connection _con;
  volatile boolean shutdown = false;

  public static void main(String[] args) {
    if (args.length < 2) {
      logger.error("Insufficient parameters. Exiting!");
      return;
    }
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    Worker w = new Worker();
    w.start(host, port);
    logger.debug("Closing worker");
  }

  private void start(String host, int port) {
    //connect to server and listen for commands
    logger.debug("Connecting to Server...");
    try (Connection con = new Connection(host, port, this::messageHandler, this::closeHandler)) {
      _con = con;
      logger.debug("Connected! Initializing listener.");
      Thread t = new Thread(con);
      t.start();
      logger.debug("Waiting for workload...");
      while (!shutdown)
        TimeUnit.MILLISECONDS.sleep(100);
    } catch (Exception e) {
      logger.error("Error occurred during worker startup");
      logger.error(Exceptions.getStackTrace(e));
    } finally {
      logger.debug("Listener terminated");
    }
  }

  private void closeHandler() {
    logger.debug("Connection notify of 'closed'");
  }

  private void messageHandler(byte[] data) {
    logger.debug("Got message");
    try {
      Message msg = Message.deserialize(data);
      if (msg.type != Type.Workload) {
        logger.error("Server message not a 'Workload'. Ignoring");
        return;
      }
      ActivityChunk work = ActivityChunk.deserialize(msg.data);
      process(work);
    } catch (Exception e) {
      logger.error("Error processing server message. Ignoring.");
      logger.error(Exceptions.getStackTrace(e));
    }
  }

  /**
   * Processes an activity chunk (submits it to the workers and sends back the result)
   *
   * @param work The workload to process
   */
  private void process(ActivityChunk work) {
    logger.debug("Got workload " + work.chunkId + "/" + work.activityId + " with " + work.waypoints.size() + " " +
      "waypoints");
    //prepare locker for result
    LockingContainer<ReductionResult> lc = new LockingContainer<>();
    //submit to workers
    pool.submit(() -> {
      ReductionResult result = Calculator.calc(work);
      lc.set(result);
    });
    //wait for the result and send it to the server
    try {
      ReductionResult rc = lc.waitTillSet();
      sendResult(rc);
    } catch (InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  private void sendResult(ReductionResult rc) throws InterruptedException {
    try {
      Message msg = new Message(Type.ReductionResult, rc.serialize());
      byte[] ser = msg.serialize();
      _con.send(ser);
    } catch (IOException e) {
      logger.error("Error during result serialization");
      throw new RuntimeException(e);
    }
  }
}
