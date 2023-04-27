package org.opa.ds23.gpxr.common.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Connects to a server and sends messages. Can also separately consume messages from the server.
 */
public class MiniClient implements AutoCloseable {

  private final int _port;
  private Consumer<byte[]> _handler;
  private Thread _listener;
  private final Socket _c;

  public MiniClient(int port, Consumer<byte[]> handler) throws IOException {
    _port = port;
    _c = new Socket("localhost", _port);
    _handler = handler;

  }

  public void send(byte[] bytes) throws IOException {
    Protocol.send(new DataOutputStream(_c.getOutputStream()), bytes);
  }

  @Override
  public void close() throws Exception {
    _c.close();
  }


}
