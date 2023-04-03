package org.ds23.gpxr.srv;

import java.util.Properties;

/**
 * Server Context
 */
class Ctx extends Properties {
  static final String WORKER_COUNT = "worker-count";
  static final int WORKER_COUNT_DEF = 5;
  private static Ctx _i = new Ctx();

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
