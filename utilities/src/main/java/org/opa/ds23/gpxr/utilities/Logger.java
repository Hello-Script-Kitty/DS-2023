package org.opa.ds23.gpxr.utilities;

/**
 * Log4J2-like logger (can be replaced, with minimal changes, with a real Log4J2 logger). We implement this in order
 * to have a way of outputting messages to the console (not proper logging).
 */
public class Logger {
  private final String c;

  Logger(String name) {
    c = name;
  }

  static Logger forClass(Class clazz) {
    return new Logger(clazz.getName());
  }

  public void debug(String msg) {
    std(msg);
  }

  public void info(String msg) {
    std(msg);
  }

  public void error(String msg) {
    err(msg);
  }

  private void std(String msg) {
    System.out.println(msg);
  }

  private void err(String msg) {
    System.err.println(msg);
  }
}
