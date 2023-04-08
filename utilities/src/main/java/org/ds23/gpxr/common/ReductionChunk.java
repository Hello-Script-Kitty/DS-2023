package org.ds23.gpxr.common;

import java.io.Serializable;

/**
 * Results of a reduction
 */
public class ReductionChunk implements Serializable {

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
