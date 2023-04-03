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

  private Srv() {
    //launch workers
    int wcount = Ctx.get().getInt(Ctx.WORKER_COUNT, Ctx.WORKER_COUNT_DEF);
    logger.info("Worker count: " + wcount);

    wm = new WorkerMgr(wcount);
    //start listener
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
