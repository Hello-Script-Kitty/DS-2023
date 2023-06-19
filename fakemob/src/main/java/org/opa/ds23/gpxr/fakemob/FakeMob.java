package org.opa.ds23.gpxr.fakemob;

import org.opa.ds23.gpxr.common.messaging.ActivityMsg;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;
import org.opa.ds23.gpxr.utilities.Exceptions;
import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FakeMob {

  static final Logger logger = LogManager.getLogger(FakeMob.class);
  static final ExecutorService es = Executors.newCachedThreadPool();

  public static void main(String[] args) throws InterruptedException {
    logger.debug("Sending " + args.length + " files...");
    for (String file : args) {
      new Thread(() -> sendFile(new File(file))).start();
    }
  }

  static private void sendFile(File f) {
    if (!f.exists()) {
      logger.error("File does not exist, ignoring " + f.getName());
      return;
    }
    Connection connection = null;
    try {
      logger.debug("Sending " + f.getName());
      //create connection
      connection = new Connection(new Socket("localhost", 8000), null, null);
      es.execute(connection);
      ActivityMsg act = new ActivityMsg(Files.readString(f.toPath()));
      Message msg = new Message(Type.Activity, act.serialize());
      connection.send(msg.serialize());
      logger.debug("Done sending file " + f.getName());
    } catch (IOException | InterruptedException e) {
      logger.error("An error occured while sending " + f.getName());
      logger.error(Exceptions.getStackTrace(e));
    } finally {
      if (connection != null)
        connection.shutdown();
    }
  }
}
