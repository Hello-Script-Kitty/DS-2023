package org.opa.ds23.gpxr.srv;

import org.opa.ds23.gpxr.common.data.*;
import org.opa.ds23.gpxr.common.messaging.ActivityMsg;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Protocol;
import org.opa.ds23.gpxr.utilities.Exceptions;
import org.opa.ds23.gpxr.utilities.LockingContainer;
import org.opa.ds23.gpxr.utilities.LogManager;
import org.opa.ds23.gpxr.utilities.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * Main Server Class (server app entry point)
 */
public class Srv {
  private static final Logger logger = LogManager.getLogger(Srv.class);

  private WorkerMgr wm;
  private static Srv app;
  private final MobAppMgr appSrv;
  private Queue<Activity> incoming = new LinkedList<>();

  private Srv() throws IOException, InterruptedException {
    //launch worker manager
    int wm_port = Ctx.get().getInt(Ctx.WM_PORT, Ctx.WM_PORT_DEF);
    logger.info("Worker control port: " + wm_port);

    wm = new WorkerMgr(wm_port);
    //start mobile app listener
    logger.debug("Starting mobile app service");
    int srv_port = Ctx.get().getInt(Ctx.SRV_PORT, Ctx.SRV_PORT_DEF);
    appSrv = new MobAppMgr(srv_port);
    logger.debug("Starting data pump");
    try {
      while (true) {
        Thread.sleep(100);
        if (incoming.size() > 0) {
          Activity data = incoming.remove();
          //convert to single chunk and send to a worker
          ActivityChunk chunk = DataUtils.toChunk(data);
          wm.submitReduction(chunk);
        }
      }
    } finally {
      logger.debug("Ending server");
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
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

  /**
   * Update {@link Activity} from {@link ReductionResult}s
   *
   * @param act The {@link Activity} to update
   * @param all The related {@link ReductionResult}s
   */
  private void updateActivity(Activity act, LockingContainer<ReductionResult>[] all) {
    logger.debug("Reducing results for activity " + act.id);
    List<ReductionResult> results = new ArrayList<>(all.length);
    for (LockingContainer<ReductionResult> cf_res : all) {
      try {
        results.add(cf_res.waitTillSet());
      } catch (InterruptedException | TimeoutException ignored) {
      }
    }
    ReductionResult r = results.stream().reduce(new ReductionResult(act), (p, n) -> {
      p.totDist += n.totDist;
      p.intervals += n.intervals;
      p.totClimb += n.totClimb;
      p.totTimeSec += n.totTimeSec;
      return p;
    });
    r.calcAvgs();
    synchronized (logger) {
      logger.debug("Activity results");
      logger.debug("----------------");
      logger.debug("Activity id: " + act.id);
      logger.debug("Creator: " + act.creator);
      logger.debug("Date: " + act.date);
      logger.debug("Duration (sec): " + r.totTimeSec);
      logger.debug("Distance (m): " + r.totDist);
      logger.debug("Samples: " + r.intervals);
      logger.debug("Avg speed (km/h): " + r.avgSpeed);
      logger.debug("Total meters ascended: " + r.totClimb);
    }
    act.result = r;
  }

  class MobAppMgr {
    private static final Logger logger = LogManager.getLogger(MobAppMgr.class);
    private final ServerSocket _srv;

    MobAppMgr(int port) throws IOException {
      //launch control service
      _srv = new ServerSocket(port);
      _srv.setReuseAddress(true);
      //spawn listening thread
      Thread listener = new Thread(() -> {
        while (true) {
          try {
            Socket c = _srv.accept();
            //spawn simple handling mechanism
            Thread tch = new Thread(() -> connectionHandler(c));
            tch.start();
          } catch (IOException e) {
            logger.error("Failed to accept a connection. Ignoring.");
          }
        }
      });
      listener.start();
    }

    private void connectionHandler(Socket c) {
      try {
        logger.debug("Got connection from a mobile app");
        //get GPX data
        byte[] data = Protocol.receive(c.getInputStream());
        c.close();
        Message msg = Message.deserialize(data);
        if (msg.type != Type.Activity) {
          logger.debug("Mobile App message not an 'Activity'. Ignoring.");
          return;
        }
        //get our DTO (Activity)
        ActivityMsg amsg = ActivityMsg.deserialize(msg.data);
        Activity act = ActivityParser.parse(amsg.gpxContent);
        synchronized (Ctx.activities) {
          Ctx.activities.put(act.id, act);
        }
        logger.debug("Submitting activity to workers");
        //submit for processing
        LockingContainer<ReductionResult>[] all = wm.submitReductions(act);
        logger.debug("Waiting for results");
        try {
          //"join"
          for (LockingContainer<ReductionResult> lc : all) {
            lc.waitTillSet(5000 * 60);
          }
        } catch (TimeoutException e) {
          synchronized (logger) {
            logger.error("Workload processing timed out");
            logger.error("Activity id: " + act.id);
            logger.error("Activity creator: " + act.creator);
            logger.error("Activity date: " + act.date);
          }
        }
        //reduce results -> update activity
        updateActivity(act, all);
      } catch (Exception e) {
        logger.error("Failed to process Mobile App message. Ignoring.");
        logger.error(Exceptions.getStackTrace(e));
      }
    }
  }
}
