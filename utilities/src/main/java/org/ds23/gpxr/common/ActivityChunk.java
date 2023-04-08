package org.ds23.gpxr.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActivityChunk implements Serializable {
  List<Waypoint> waypoints = new ArrayList<>();
}
