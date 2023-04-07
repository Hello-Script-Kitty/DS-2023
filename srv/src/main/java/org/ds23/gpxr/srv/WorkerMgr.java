package org.ds23.gpxr.srv;

import org.ds23.gpxr.utilities.LogManager;
import org.ds23.gpxr.utilities.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Worker Manager
 */
public class WorkerMgr {
  private static final Logger logger = LogManager.getLogger(WorkerMgr.class);

  private static List<Worker> _pl;

  /**
   * Initialize with the required count of workers (will take some time to start and initialize each worker)
   *
   * @param workerCount Count of required workers
   */
  WorkerMgr(int workerCount) {
    _pl = new ArrayList<>(workerCount);
    //start workers
    for (int i = 0; i < workerCount; i++) {
      logger.info("Launching worker #" + i);
      _pl.add(new Worker());
    }
  }

  /**
   * Worker controller
   */
  private class Worker {
    Process proc; //hold process
    Map<String, RedWork> workloads; //workloads assigned to worker (currently working)
  }
}
