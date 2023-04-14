package org.opa.ds23.common;

import java.io.Serializable;

/**
 * Represents a coordinate with elevation (it's a point on earth!)
 */
public class Coordinate3D implements Serializable {
  private static final long serialVersionUID = 5386796983159887816L;
  double lat;
  double lon;
  double elevation;
}
