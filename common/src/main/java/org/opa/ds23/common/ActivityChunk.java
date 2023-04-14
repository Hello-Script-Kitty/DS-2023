package org.opa.ds23.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chunk of waypoint data on which a worker can make calculations and return results.
 */
public class ActivityChunk implements Serializable {
  private static final long serialVersionUID = 3092625383910625647L;
  List<Waypoint> waypoints = new ArrayList<>();
}
