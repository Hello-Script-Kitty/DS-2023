package org.ds23.gpxr.utilities;

/**
 * Dummy Log4J2 LogManager
 */
public class LogManager {
  public static Logger getLogger(Class clazz) {
    return Logger.forClass(clazz);
  }
}
