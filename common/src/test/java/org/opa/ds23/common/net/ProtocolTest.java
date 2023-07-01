package org.opa.ds23.common.net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opa.ds23.gpxr.common.messaging.ActivityMsg;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;
import org.opa.ds23.gpxr.common.net.MiniServer;
import org.opa.ds23.gpxr.utilities.Exceptions;
import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProtocolTest {
  static final Logger logger = LogManager.getLogger(ProtocolTest.class);

  private static final String MSG1 = "Hello, World!";
  private static final String MSG2 = "Whaz'up, Beijing?";

  private static List<String> received = new ArrayList<>();

  private static MiniServer srv;
  //  private static ThreadFactory tf = Executors.defaultThreadFactory();
  private static final ExecutorService execSrv = Executors.newCachedThreadPool();

  @BeforeAll
  static void setUp() throws IOException {
    System.out.println("Starting MiniServer");
    srv = new MiniServer(3800, ProtocolTest::accept);
  }

  @BeforeEach
  void setUpEach() {
    received = new ArrayList<>();
  }

  @AfterAll
  static void tearDown() throws IOException {
    srv.shutdown();
  }

  @Test
  public void test1() throws Exception {
    TimeUnit.SECONDS.sleep(3);
    System.out.println("Opening client connection");
    try (Socket sock = new Socket("localhost", 3800)) {
      System.out.println("Initializing connection object");
      Connection connection = new Connection(sock, null, null);
      System.out.println("Starting connection thread");
//      Thread ct = tf.newThread(connection);
      execSrv.execute(connection);
      TimeUnit.SECONDS.sleep(1);
      System.out.println("Sending 1st message");
      byte[] data = MSG1.getBytes(StandardCharsets.UTF_8);
      connection.send(data);
      TimeUnit.SECONDS.sleep(1);
      System.out.println("Sending 2nd message");
      data = MSG2.getBytes(StandardCharsets.UTF_8);
      connection.send(data);
      TimeUnit.SECONDS.sleep(1);
      System.out.println("Shutting down connection");
      connection.shutdown();
      TimeUnit.SECONDS.sleep(1);
//      ct.join();
      execSrv.shutdownNow();
    }
    assertEquals(2, received.size(), "received all messages");
    assertEquals(MSG1, received.get(0));
    assertEquals(MSG2, received.get(1));
  }

  //multiple messages via different connections
  @Test
  public void test2() throws Exception {
    TimeUnit.SECONDS.sleep(3);
    final int sc = 40;
    String[] msgs = new String[sc];
    for (int i = 0; i < sc; i++) {
      msgs[i] = String.format("Message #%d", i);
    }
    for (String m : msgs) {
      //send file in a thread
      execSrv.submit(() -> {
        try (Connection connection = new Connection("localhost", 3800, null, null)) {
          logger.debug("Sending " + m);
          Thread t = new Thread(connection);
          t.start();
          TimeUnit.SECONDS.sleep(1);
          byte[] data = m.getBytes(StandardCharsets.UTF_8);
          connection.send(data);
          TimeUnit.SECONDS.sleep(1);
        } catch (IOException | InterruptedException e) {
          logger.error("An error occurred while sending " + m);
          logger.error(Exceptions.getStackTrace(e));
        }
      });
    }
    execSrv.awaitTermination(10, TimeUnit.SECONDS);
//    execSrv.shutdown();
    logger.debug("all sending threads finished");
    assertEquals(sc, received.size());
  }

  //one connection sends all messages
  @Test
  public void test3() throws Exception {
    TimeUnit.SECONDS.sleep(3);
    final int sc = 40;
    String[] msgs = new String[sc];
    for (int i = 0; i < sc; i++) {
      msgs[i] = String.format("Message #%d", i);
    }
    //connect
    try (Connection connection = new Connection("localhost", 3800, null, null)) {
      execSrv.submit(connection);
      TimeUnit.SECONDS.sleep(1);
      for (String m : msgs) {
        logger.debug("Sending " + m);
        byte[] data = m.getBytes(StandardCharsets.UTF_8);
        connection.send(data);
      }
      TimeUnit.SECONDS.sleep(1);
    } catch (IOException | InterruptedException e) {
      logger.error("An error occurred while sending a message");
      logger.error(Exceptions.getStackTrace(e));
    }
    execSrv.awaitTermination(10, TimeUnit.SECONDS);
    logger.debug("sending thread finished");
    assertEquals(sc, received.size());
  }

  private static synchronized void accept(byte[] data) {
    String s = new String(data);
    System.out.println("Message received: " + s);
    received.add(s);
  }
}
