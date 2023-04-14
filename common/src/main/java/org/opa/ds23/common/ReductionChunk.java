package org.opa.ds23.common;

import java.io.Serializable;

/**
 * Results of a reduction on an activity chunk
 */
public class ReductionChunk implements Serializable {
  private static final long serialVersionUID = -2684467689749142801L;

  /**
   * Intervals reduced
   */
  public int intervals;
  /**
   * Total distance
   */
  public float totDist;
  /**
   * Average spedd
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
