package org.opa.ds23.gpxr.worker;

import org.opa.ds23.gpxr.common.data.ActivityChunk;
import org.opa.ds23.gpxr.common.data.ReductionResult;
import org.opa.ds23.gpxr.common.data.Waypoint;
import org.opa.ds23.gpxr.utilities.math.GIS;

import java.time.Duration;
import java.util.List;

class Calculator {

  static ReductionResult calc(ActivityChunk chunk) {
    ReductionResult r = new ReductionResult(chunk);
    List<Waypoint> list = chunk.waypoints;
    r.intervals = list.size() - 1;
    for (int i = 0; i < r.intervals; i++) {
      Waypoint w1 = list.get(i);
      Waypoint w2 = list.get(i + 1);
      r.totDist += GIS.distance(w1.coordinate.lat, w2.coordinate.lat,
        w1.coordinate.lon, w2.coordinate.lon,
        w1.coordinate.elevation, w2.coordinate.elevation);
      r.totTimeSec += Duration.between(w1.timestamp, w2.timestamp).getSeconds();
      r.totClimb += w2.coordinate.elevation - w1.coordinate.elevation;
    }
    r.calcAvgs();
    return r;
  }
}
