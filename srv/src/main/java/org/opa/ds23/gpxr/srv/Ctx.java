package org.opa.ds23.gpxr.srv;

import java.util.Properties;

/**
 * Server Context
 */
class Ctx extends Properties {
  static final String SRV_PORT = "srv-port";
  static final int SRV_PORT_DEF = 8080;
  static final String WM_PORT = "wm-port";
  static final int WM_PORT_DEF = 8081;
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
