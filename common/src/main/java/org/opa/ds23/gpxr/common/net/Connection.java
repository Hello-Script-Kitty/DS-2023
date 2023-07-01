package org.opa.ds23.gpxr.common.net;

import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Manages an open connection through a socket (1-to-1 connection). Can send and/or receive "messages".
 */
public class Connection implements Runnable, AutoCloseable {
  private static final Logger logger = LogManager.getLogger(Connection.class);

  private final Socket _s;
  private volatile boolean _sending = false;
  private final Consumer<byte[]> _handler;
  private final Runnable _closeHandler;
  private volatile boolean _shutdown = false;
  private final Queue<byte[]> _out = new LinkedList<>();

  public Connection(Socket socket, Consumer<byte[]> msgHandler, Runnable closeHandler) {
    _s = socket;
    _handler = msgHandler;
    _closeHandler = closeHandler;
  }

  public Connection(String host, int port, Consumer<byte[]> msgHandler, Runnable closeHandler) throws IOException {
    _s = new Socket(host, port);
    _handler = msgHandler;
    _closeHandler = closeHandler;
  }

  /**
   * Queues a message on the outgoing queue
   *
   * @param message The message to queue for sending
   */
  public void send(byte[] message) throws InterruptedException {
    if (_shutdown) {
      logger.debug("Will not queue message during shutdown");
      return;
    }
    logger.debug("Queuing a message for send");
    synchronized (_out) {
      _out.add(message);
      _sending = true;
    }
  }

  public void shutdown() {
    _shutdown = true;
  }

  @Override
  public void run() {
    //launch sending thread
    logger.debug("Spawning send thread...");
    Thread sender = new Thread(() -> {
      logger.debug("Starting sender loop");
      try {
        while (!_shutdown) {
//          logger.debug("Polling for message to send");
          //we poll the queue at intervals so that we can also check the shutdown flag
          byte[] msg;
          synchronized (_out) {
            msg = _out.poll();
            if (msg != null) {
              logger.debug("Got outgoing message from queue");
              _sending = true;
              Protocol.send(_s.getOutputStream(), msg);
              if (_out.isEmpty())
                _sending = false;
              logger.debug("Outgoing message sent!");
            } else
              TimeUnit.MILLISECONDS.sleep(200);
          }
        }
        logger.debug("Sender loop terminated");
      } catch (InterruptedException e) {
        //log the error and terminate thread
//        logger.error("Send thread polling interrupted");
      } catch (IOException e) {
//        logger.error("Failed to send message through the socket");
      } finally {
        //ensure _sending is false now!
        _sending = false;
      }
      //done
      logger.debug("Sender loop ended");
    });
//    sender.setDaemon(true);
    sender.start();

    logger.debug("Starting listening loop");
    //start listening for messages from the other side
    while (!_shutdown) {
      try {
        byte[] msg = Protocol.receive(_s.getInputStream());
        logger.debug("Received message");
        if (_handler != null)
          _handler.accept(msg);
      } catch (IOException e) {
//        logger.error("Failed to send message through the socket");
//        logger.error(Exceptions.getStackTrace(e));
//        throw new RuntimeException(e);
      }

      //fail if connection is down or thread has been interrupted
      if (!_s.isConnected() || Thread.currentThread().isInterrupted())
        _shutdown = true;
    }
    if (_closeHandler != null)
      _closeHandler.run();
    //wait for any sending to finish
    if (_sending)
      logger.debug("Will wait for sending to finish...");
    while (_sending) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException ignored) {
        logger.debug("Send wait interrupted!");
      }
    }
//    try {
//      TimeUnit.MILLISECONDS.sleep(500);
//    } catch (InterruptedException ignored) {
//    } finally {
//      _execSrv.shutdown();
//      logger.debug("Listening loop was exited");
//    }
    logger.debug("Listening loop was exited");
  }

  @Override
  public void close() {
    shutdown();
  }
}
