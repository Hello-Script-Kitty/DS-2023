package org.opa.ds23.common.net;

import org.junit.jupiter.api.*;
import org.opa.ds23.gpxr.common.net.Connection;
import org.opa.ds23.gpxr.common.net.MiniServer;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProtocolTest {

  private static final String MSG1 = "Hello, World!";
  private static final String MSG2 = "Whaz'up, Beijing?";

  private static List<String> received = new ArrayList<>();

  private static MiniServer srv;
//  private static ThreadFactory tf = Executors.defaultThreadFactory();
  private static ExecutorService execSrv = Executors.newCachedThreadPool();

  @BeforeAll
  static void setUp() throws IOException {
    System.out.println("Starting MiniServer");
    srv = new MiniServer(3800, ProtocolTest::accept);
  }

  @AfterAll
  static void tearDown() throws IOException {
    srv.shutdown();
  }

  @Test
  public void test1() throws Exception {
    Thread.sleep(3000);
    System.out.println("Opening client connection");
    try (Socket sock = new Socket("localhost", 3800)) {
      System.out.println("Initializing connection object");
      Connection connection = new Connection(sock, null, null);
      System.out.println("Starting connection thread");
//      Thread ct = tf.newThread(connection);
      execSrv.submit(connection);
      Thread.sleep(1000);
      System.out.println("Sending 1st message");
      byte[] data = new String(MSG1).getBytes(StandardCharsets.UTF_8);
      connection.send(data);
      Thread.sleep(1000);
      System.out.println("Sending 2nd message");
      data = new String(MSG2).getBytes(StandardCharsets.UTF_8);
      connection.send(data);
      Thread.sleep(1000);
      System.out.println("Shutting down connection");
      connection.shutdown();
      Thread.sleep(1000);
//      ct.join();
      execSrv.shutdownNow();
    }
    assertEquals(2, received.size(), "received all messages");
    assertEquals(MSG1, received.get(0));
    assertEquals(MSG2, received.get(1));
  }

  private static synchronized void accept(byte[] data) {
    String s = new String(data);
    System.out.println("Message received: " + s);
    received.add(s);
  }
}
