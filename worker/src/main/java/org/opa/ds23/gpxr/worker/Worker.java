package org.opa.ds23.gpxr.worker;

import org.opa.ds23.gpxr.common.data.ActivityChunk;
import org.opa.ds23.gpxr.common.data.ReductionResult;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;
import org.opa.ds23.gpxr.utilities.Exceptions;
import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Main Worker Class (worker app entry point)
 */
public class Worker {
  private static final Logger logger = LogManager.getLogger(Worker.class);
  private final ExecutorService execSrv = Executors.newCachedThreadPool();
  private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  Connection _con;
  volatile boolean shutdown = false;

  public static void main(String[] args) throws IOException {
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
    try (Socket sock = new Socket(host, port)) {
      _con = new Connection(sock, this::messageHandler, this::closeHandler);
      logger.debug("Connected! Initializing listener.");
      execSrv.execute(_con);
      logger.debug("Waiting for workload...");
      while (!shutdown)
        TimeUnit.SECONDS.sleep(1);
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
      CompletableFuture.supplyAsync(() -> Calculator.calc(work), pool)
        .thenAcceptAsync(r -> sendResult(r), execSrv);
    } catch (Exception e) {
      logger.error("Error processing server message. Ignoring.");
      logger.error(Exceptions.getStackTrace(e));
    }
  }

  private void sendResult(ReductionResult rc) {
    try {
      Message msg = new Message(Type.ReductionResult, rc.serialize());
      _con.send(msg.serialize());
    } catch (IOException | InterruptedException e) {
      if (e instanceof IOException)
        logger.error("Error during result serialization");
      else
        logger.error("Error during result message submission to send");
      throw new CompletionException(e);
    }
  }
}
