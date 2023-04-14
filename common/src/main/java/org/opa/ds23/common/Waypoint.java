package org.opa.ds23.common;

import java.io.Serializable;
import java.time.Instant;

/**
 * A waypoint
 */
public class Waypoint implements Serializable {
  private static final long serialVersionUID = 7470866592699742458L;
  Coordinate3D coordinate;
  Instant timestamp;
}
