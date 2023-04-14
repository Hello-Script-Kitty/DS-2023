package org.opa.ds23.common;

import java.io.Serializable;

/**
 * Encapsulates different types of messages commit from workers
 */
public class WorkerMsg implements Serializable {
  private static final long serialVersionUID = -5211236891553050622L;

  /**
   * Declares message types
   */
  public enum Type {
    /**
     * Worker enrollment message
     */
    Enroll,
    /**
     * Workload result message
     */
    Result;
  }

  /**
   * The message type
   */
  public Type type;

  /**
   * The message data. This is a serialized object, that it's type depends on
   */
  byte[] data;
}
