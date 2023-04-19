package org.opa.ds23.common;

import org.ds23.gpxr.utilities.LogManager;
import org.ds23.gpxr.utilities.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MiniServer {
  private static final Logger logger = LogManager.getLogger(MiniServer.class);

  private final ServerSocket _srv;
  private int _port;
  private ExecutorService _execSrv = Executors.newFixedThreadPool(4);
  private final Srv _listener;
  private Consumer<byte[]> _handler;

  public MiniServer(int port, Consumer<byte[]> handler) throws IOException {
    if (handler == null)
      throw new IllegalArgumentException("Handler is null");
    _port = port;
    _handler = handler;
    _srv = new ServerSocket(_port);
    _srv.setReuseAddress(true);

    _listener = new Srv();
    _execSrv.submit(_listener);
  }

  public void shutdown() throws IOException {
//    _listener.interrupt();
    _srv.close();
    _execSrv.shutdownNow();
  }

  private class Srv extends Thread {

    boolean shutdown = false;

    @Override
    public void run() {
      while (!shutdown && !isInterrupted()) {
        try {
          Socket c = _srv.accept();
          //FIXME spawn thread for worker subscription
          _execSrv.execute(() -> {
            while (c.isConnected()) {
              //handle message
              try {
                _handler.accept(Protocol.receive(new DataInputStream(c.getInputStream())));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          });
        } catch (IOException e) {
          logger.error("Failed to accept connection");
        }
      }
    }
  }
}
