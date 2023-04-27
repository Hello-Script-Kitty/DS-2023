package org.opa.ds23.gpxr.common.data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Results of a reduction on an activity chunk
 */
public class ReductionChunk implements Serializable {
  @Serial
  private static final long serialVersionUID = -2684467689749142801L;

  /**
   * Id of submitted activity chunk
   */
  public String chunkId;
  /**
   * Intervals reduced
   */
  public int intervals;
  /**
   * Total distance
   */
  public float totDist;
  /**
   * Average speed
   */
  public float avgSpeed;
  /**
   * Total climb
   */
  public float totClimb;
  /**
   * Total time in seconds
   */
  public int totTimeSec;
}
