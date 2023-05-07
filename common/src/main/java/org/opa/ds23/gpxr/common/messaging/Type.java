package org.opa.ds23.gpxr.common.messaging;

import org.opa.ds23.gpxr.common.data.ActivityChunk;
import org.opa.ds23.gpxr.common.data.ReductionResult;

/**
 * Declares message types. Dictates which class the message byte content will deserialize to.
 */
public enum Type {
  /**
   * Activity message. Deserializes to {@link ActivityMsg}.
   */
  Activity(ActivityMsg.class),
//  /**
//   * Worker status message. Deserializes to {@link WorkerStatusMsg}.
//   */
//  WorkerStatus,
  /**
   * Reduction result. Deserializes to {@link ReductionResult}.
   */
  ReductionResult(ReductionResult.class),
  /**
   * Workload submission. Deserializes to {@link ActivityChunk}.
   */
  Workload(ActivityChunk.class);

  public final Class type;

  private Type(Class clazz) {
    type = clazz;
  }
}
