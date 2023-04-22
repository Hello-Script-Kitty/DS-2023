package org.opa.ds23.common;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProtocolTest {

  private static final String MSG1 = "Hello, World!";
  private static final String MSG2 = "Whaz'up, Beijing?";

  private static List<String> received = new ArrayList<>();

  private static MiniServer srv;

  @BeforeAll
  static void setUp() throws IOException {
    srv = new MiniServer(3800, ProtocolTest::accept);
  }

  @AfterAll
  static void tearDown() throws IOException {
    srv.shutdown();
  }

  @Test
  public void test1() throws Exception {
    Thread.sleep(3000);
    try (MiniClient client = new MiniClient(3800)) {
      byte[] data = new String(MSG1).getBytes(StandardCharsets.UTF_8);
      client.send(data);
      Thread.sleep(1000);
      data = new String(MSG2).getBytes(StandardCharsets.UTF_8);
      client.send(data);
      Thread.sleep(1000);
    }
    assertEquals(MSG1, received.get(0));
    assertEquals(MSG2, received.get(1));
  }

  private static synchronized void accept(byte[] data) {
    String s = new String(data);
    System.out.println("Message received: " + s);
    received.add(s);
  }
}
