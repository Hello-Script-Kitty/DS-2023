package org.opa.ds23.gpxr.common.data;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {
  public static ActivityChunk toChunk(Activity activity) {
    ActivityChunk chunk = new ActivityChunk(activity);
    chunk.waypoints = activity.waypoints;
    return chunk;
  }

  public static List<ActivityChunk> toChunks(Activity activity, int parts) {
    ArrayList<ActivityChunk> chunks = new ArrayList<>(parts);
    List<Waypoint> wp = activity.waypoints;
    int count = Math.floorDiv(wp.size() - (parts - 1), parts);
    for (int i = 0; i < parts; i++) {
      int start = parts * i;
      int end = Math.min(start + count + 1, wp.size());
      ActivityChunk chunk = new ActivityChunk(activity);
      List<Waypoint> newlist = wp.subList(start, end);
      chunk.waypoints.addAll(newlist);
      chunks.add(chunk);
    }
    return chunks;
  }
}
