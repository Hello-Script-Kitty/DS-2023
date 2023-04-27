package org.opa.ds23.gpxr.common.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * A waypoint
 */
public class Waypoint implements Serializable {
  @Serial
  private static final long serialVersionUID = 7470866592699742458L;
  public Coordinate3D coordinate;
  public Instant timestamp;

  public Waypoint(double lat, double lon, float ele, LocalDateTime time) {
    coordinate = new Coordinate3D(lat, lon, ele);
    timestamp = time;
  }
}
