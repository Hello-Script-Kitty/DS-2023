package org.opa.ds23.common;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MiniClient implements AutoCloseable {

  private final int _port;
  private final Socket _c;

  public MiniClient(int port) throws IOException {
    _port = port;
    _c = new Socket("localhost", _port);
  }

  public void send(byte[] bytes) throws IOException {
    Protocol.send(new DataOutputStream(_c.getOutputStream()), bytes);
  }

  @Override
  public void close() throws Exception {
    _c.close();
  }
}
