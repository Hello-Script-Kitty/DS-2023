package org.opa.ds23.gpxr.common.data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a coordinate with elevation (it's a point on earth!)
 */
public class Coordinate3D implements Serializable {
  @Serial
  private static final long serialVersionUID = 5386796983159887816L;
  double lat;
  double lon;
  double elevation;

  public Coordinate3D(double lat, double lon, float ele) {
    this.lat = lat;
    this.lon = lon;
    this.elevation = ele;
  }
}
