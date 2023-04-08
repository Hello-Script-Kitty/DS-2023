package org.ds23.gpxr.srv;

import org.ds23.gpxr.utilities.LogManager;
import org.ds23.gpxr.utilities.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main Server Class (server app entry point)
 */
public class Srv {
  private static final Logger logger = LogManager.getLogger(Srv.class);

  private WorkerMgr wm;
  private static Srv app;
  private ActivityMgr activities = new ActivityMgr();

  private Srv() {
    //launch worker manager
    int wm_port = Ctx.get().getInt(Ctx.WM_PORT, Ctx.WM_PORT_DEF);
    logger.info("Worker control port: " + wm_port);

    wm = new WorkerMgr(wm_port);
    //start mobile app listener
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Running Server");
    //determine configuration
    File propsFile = new File("server.properties");
    if (propsFile.exists()) {
      logger.debug("Will load properties from " + propsFile.getAbsolutePath());
      Ctx.get().load(new FileReader(propsFile));
    } else
      logger.debug("Properties file " + propsFile.getAbsolutePath() + " not found. Default values will be used!");
    //start server
    app = new Srv();
  }


}
