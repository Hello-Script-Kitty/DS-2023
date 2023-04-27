package org.opa.ds23.gpxr.common.messaging;

import java.io.Serializable;

public class Message implements Serializable {
  private static final long serialVersionUID = -3910813626470329656L;

  /**
   * The message type
   */
  public Type type;

  /**
   * The message data. This is a serialized object, that it's type depends on
   */
  byte[] data;
}
