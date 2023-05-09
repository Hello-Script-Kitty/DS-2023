package org.opa.ds23.gpxr.srv;

import com.sun.source.tree.SynchronizedTree;
import org.opa.ds23.gpxr.common.data.Activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server Context. Execution parameters and central storage.
 */
class Ctx extends Properties {
  static final String SRV_PORT = "srv-port";
  static final int SRV_PORT_DEF = 8080;
  static final String WM_PORT = "wm-port";
  static final int WM_PORT_DEF = 8081;
  static final Map<String, Activity> activities = new HashMap<>(); //all submitted activities registry
  //cached thread pool for all server short-lived threads
  static final ExecutorService es = Executors.newCachedThreadPool();
  private static final Ctx _i = new Ctx();

  private Ctx() {
  }

  public static Ctx get() {
    return _i;
  }

  /**
   * Return integer value from property. If property key does not exist or string value is not parsable, a default
   * value will be returned
   *
   * @param key The name of the property
   * @param defaultValue The default value if the property was not found or was not parsable as an integer
   * @return
   */
  public int getInt(String key, int defaultValue) {
    String v = getProperty(key);
    if (v == null)
      return defaultValue;
    try {
      int vi = Integer.parseInt(v);
      return vi;
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }
}
