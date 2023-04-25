package org.opa.ds23.common.net;

import org.ds23.gpxr.utilities.LogManager;
import org.ds23.gpxr.utilities.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Manages an open connection through a socket (1-to-1 connection). Can send and/or receive "messages". The outgoing
 * messages are collected in a queue so that they are sent serially.
 */
public class Connection implements Runnable {
  private static final Logger logger = LogManager.getLogger(Connection.class);

  private final Socket _s;
  private final Consumer<byte[]> _handler;
  private volatile boolean shutdown = false;
  BlockingQueue<byte[]> _out = new LinkedBlockingQueue<>();

  public Connection(Socket socket, Consumer<byte[]> msgHandler) {
    _s = socket;
    _handler = msgHandler;
  }

  /**
   * Queues a message on the outgoing queue
   *
   * @param message The message to queue for sending
   */
  public synchronized void send(byte[] message) {
    if (shutdown)
      return;
    try {
      _out.put(message);
    } catch (InterruptedException ignored) {
    }
  }

  public void shutdown() {
    shutdown = true;
  }

  @Override
  public void run() {
    //launch sending thread
    Executors.defaultThreadFactory().newThread(() -> {
      try {
        while (!shutdown) {
          //poll the queue at intervals so that we can also observe the shutdown flag
          byte[] msg = _out.poll(200, TimeUnit.MILLISECONDS);
          if (msg != null)
            _s.getOutputStream().write(msg);
        }
      } catch (InterruptedException e) {
        //log the error and terminate thread
        logger.error("Send thread polling interrupted");
      } catch (IOException e) {
        logger.error("Failed to send message through the socket");
      }
      //done
    });

    //start listening for messages from the other side
    while (!shutdown) {
      try {
        byte[] msg = Protocol.receive(new DataInputStream(_s.getInputStream()));
        _handler.accept(msg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      //fail if connection is down or thread has been interrupted
      shutdown &= _s.isConnected() && !Thread.currentThread().isInterrupted();
    }
  }
}
