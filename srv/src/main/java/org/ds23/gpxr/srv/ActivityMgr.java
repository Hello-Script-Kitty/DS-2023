package org.ds23.gpxr.srv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps activities and resulting data
 */
public class ActivityMgr {

  Map<String, Activity> byId = new HashMap<>();
  Map<String, List<Activity>> byUser = new HashMap<>();

  synchronized void addActivity(Activity act) {
    //FIXME add to internal structures
  }
}
