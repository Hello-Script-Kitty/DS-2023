package org.ds23.gpxr.common;

import java.io.Serializable;
import java.time.Instant;

/**
 * A waypoint
 */
public class Waypoint implements Serializable {
  Coordinate coordinate;
  float elev;
  Instant timestamp;
}
