package org.opa.ds23.gpxr.srv;

import org.opa.ds23.gpxr.common.messaging.ActivityMsg;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DummyMobApp {
  private static ExecutorService execSrv = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    System.out.println("Opening client connection");
    Connection connection = null;
    try (Socket sock = new Socket("localhost", 8000)) {
      System.out.println("Initializing connection object");
      connection = new Connection(sock, null, null);
      execSrv.execute(connection);
      TimeUnit.SECONDS.sleep(1);
      ActivityMsg act = new ActivityMsg();
      act.gpxContent = Files.readString(Path.of("C:\\Utils\\tmp\\gpxs\\route1.gpx"));
      Message msg = new Message(Type.Activity, act.serialize());
      connection.send(msg.serialize());
      TimeUnit.SECONDS.sleep(2);
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null)
        connection.shutdown();
    }
  }
}
