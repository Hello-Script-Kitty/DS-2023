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
import java.nio.file.Files;

public class FakeMob {

  static final Logger logger = LogManager.getLogger(FakeMob.class);

  public static void main(String[] args) throws InterruptedException {
    logger.debug("Sending " + args.length + " files...");
    for (String file : args) {
      //send file in a thread
      new Thread(() -> {
        try {
          sendFile(new File(file));
        } catch (Exception ignored) {
        }
      }).start();
    }
  }

  static private void sendFile(File f) throws Exception {
    if (!f.exists()) {
      logger.error("File does not exist, ignoring " + f.getName());
      return;
    }
    try (Connection connection = new Connection("localhost", 8000, null, null)) {
      logger.debug("Sending " + f.getName());
      Thread t = new Thread(connection);
      t.start();
      ActivityMsg act = new ActivityMsg(Files.readString(f.toPath()));
      Message msg = new Message(Type.Activity, act.serialize());
      connection.send(msg.serialize());
      logger.debug("Done sending file " + f.getName());
    } catch (IOException | InterruptedException e) {
      logger.error("An error occured while sending " + f.getName());
      logger.error(Exceptions.getStackTrace(e));
    }
  }
}
