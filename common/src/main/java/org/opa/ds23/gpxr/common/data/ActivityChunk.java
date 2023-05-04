package org.opa.ds23.gpxr.common.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a chunk of waypoint data on which a worker should make calculations and return results.
 */
public class ActivityChunk implements Serializable {
  @Serial
  private static final long serialVersionUID = 3092625383910625647L;
  /**
   * The id of this submission
   */
  public final String chunkId = UUID.randomUUID().toString();
  /**
   * The submitted waypoint data
   */
  public List<Waypoint> waypoints = new ArrayList<>();
}
