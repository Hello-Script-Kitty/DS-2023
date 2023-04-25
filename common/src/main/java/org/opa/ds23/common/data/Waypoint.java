package org.opa.ds23.common.data;

import org.opa.ds23.common.data.Coordinate3D;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * A waypoint
 */
public class Waypoint implements Serializable {
  @Serial
  private static final long serialVersionUID = 7470866592699742458L;
  Coordinate3D coordinate;
  Instant timestamp;
}
