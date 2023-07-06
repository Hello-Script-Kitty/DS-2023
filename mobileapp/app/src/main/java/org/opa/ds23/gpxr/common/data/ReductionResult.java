package org.opa.ds23.gpxr.common.data;

import java.io.*;

/**
 * Results of a reduction on an activity chunk
 */
public class ReductionResult implements Serializable {
//  @Serial
  private static final long serialVersionUID = -2684467689749142801L;

  /**
   * Id of submitted activity chunk. May be null if this object is total result of activity.
   */
  public String chunkId;
  /**
   * Id of activity this chunk belongs to
   */
  public final String activityId;
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

  public ReductionResult(ActivityChunk ac) {
    activityId = ac.activityId;
    chunkId = ac.chunkId;
  }

  public ReductionResult(Activity act) {
    activityId = act.id;
  }

  public static ReductionResult deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return (ReductionResult) ois.readObject();
    }
  }

  public byte[] serialize() throws IOException {
    return Util.serialize(this);
  }

  public void calcAvgs() {
    //calculate avg speed in km/h
    float km = totDist / 1000;
    float hours = totTimeSec / 3600f;
    avgSpeed = km / hours;
  }
}
