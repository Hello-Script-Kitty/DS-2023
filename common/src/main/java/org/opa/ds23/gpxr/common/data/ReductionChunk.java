package org.opa.ds23.gpxr.common.data;

import java.io.*;

/**
 * Results of a reduction on an activity chunk
 */
public class ReductionChunk implements Serializable {
  @Serial
  private static final long serialVersionUID = -2684467689749142801L;

  /**
   * Id of submitted activity chunk
   */
  public final String chunkId;
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

  public ReductionChunk(ActivityChunk ac) {
    chunkId = ac.chunkId;
  }

  public static ReductionChunk deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return (ReductionChunk) ois.readObject();
    }
  }

  public byte[] serialize() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(this);
    }
    return baos.toByteArray();
  }
}
