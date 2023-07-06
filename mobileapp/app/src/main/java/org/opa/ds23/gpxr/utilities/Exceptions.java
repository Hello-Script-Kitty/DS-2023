package org.opa.ds23.gpxr.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {
  public static String getStackTrace(Throwable ex) {
    StringWriter errors = new StringWriter();
    ex.printStackTrace(new PrintWriter(errors));
    return errors.toString();
  }
}
