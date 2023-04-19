package org.opa.ds23.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ProtocolTest {

  private static MiniServer srv;

  @BeforeEach
  void setUp() throws IOException {
    srv = new MiniServer(3800, ProtocolTest::accept);
  }

  @AfterEach
  void tearDown() throws IOException {
    srv.shutdown();
  }

  @Test
  public void test1() throws InterruptedException {
    Thread.sleep(3000);
  }

  private static void accept(byte[] data) {
    String s = new String(data);
    System.out.println(s);
  }
}
