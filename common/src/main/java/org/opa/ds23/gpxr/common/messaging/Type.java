package org.opa.ds23.gpxr.common.messaging;

import org.opa.ds23.gpxr.common.data.ActivityChunk;

/**
 * Declares message types. Dictates which class the message byte content will deserialize to.
 */
public enum Type {
  /**
   * Activity message. Deserializes to {@link ActivityMsg}.
   */
  Activity,
  /**
   * Worker status message. Deserializes to {@link WorkerStatusMsg}.
   */
  WorkerStatus,
  /**
   * Reduction result. Deserializes to {@link org.opa.ds23.gpxr.common.data.ReductionChunk}.
   */
  ReductionChunk,
  /**
   * Workload submission. Deserializes to {@link ActivityChunk}.
   */
  Workload;
}
