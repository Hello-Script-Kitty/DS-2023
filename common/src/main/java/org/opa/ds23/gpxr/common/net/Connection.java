package org.opa.ds23.gpxr.common.net;

import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;

/**
 * Manages an open connection through a socket (1-to-1 connection). Can send and/or receive "messages".
 */
public class Connection implements Runnable {
  private static final Logger logger = LogManager.getLogger(Connection.class);

  private final Socket _s;
  private final Consumer<byte[]> _handler;
  private final Runnable _closeHandler;
  private volatile boolean shutdown = false;
  SynchronousQueue<byte[]> _out = new SynchronousQueue<>(true);
  private final ExecutorService _execSrv = Executors.newCachedThreadPool();

  public Connection(Socket socket, Consumer<byte[]> msgHandler, Runnable closeHandler) {
    _s = socket;
    _handler = msgHandler;
    _closeHandler = closeHandler;
  }

  /**
   * Queues a message on the outgoing queue
   *
   * @param message The message to queue for sending
   */
  public synchronized void send(byte[] message) throws InterruptedException {
    if (shutdown) {
      logger.debug("Will not queue message during shutdown");
      return;
    }
    logger.debug("Queuing a message for send");
    _out.put(message);
  }

  public void shutdown() {
    shutdown = true;
  }

  @Override
  public void run() {
    //launch sending thread
    logger.debug("Spawning send thread...");
    _execSrv.submit(() -> {
      logger.debug("Starting sender loop");
      try {
        while (!shutdown) {
          logger.debug("Polling for message to send");
          //we poll the queue at intervals so that we can also check the shutdown flag
          byte[] msg = _out.poll();
          if (msg != null) {
            logger.debug("Got outgoing message from queue");
            Protocol.send(_s.getOutputStream(), msg);
          } else
            Thread.sleep(200);
        }
        logger.debug("Sender loop terminated");
      } catch (InterruptedException e) {
        //log the error and terminate thread
//        logger.error("Send thread polling interrupted");
      } catch (IOException e) {
//        logger.error("Failed to send message through the socket");
      }
      //done
      logger.debug("Sender loop ended");
    });

    logger.debug("Starting listening loop");
    //start listening for messages from the other side
    while (!shutdown) {
      try {
        byte[] msg = Protocol.receive(_s.getInputStream());
        logger.debug("Received message");
        _handler.accept(msg);
      } catch (IOException e) {
//        logger.error("Failed to send message through the socket");
//        logger.error(Exceptions.getStackTrace(e));
//        throw new RuntimeException(e);
      }

      //fail if connection is down or thread has been interrupted
      shutdown &= _s.isConnected() && !Thread.currentThread().isInterrupted();
    }
    if (_closeHandler != null)
      _closeHandler.run();
    logger.debug("Listening loop was exited");
  }
}
