package org.opa.ds23.gpxr.common.data;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * A Java representation of a GPX file contents
 */
public class Activity implements Serializable {
  @Serial
  private static final long serialVersionUID = -3781403728812799646L;

  public final String id = UUID.randomUUID().toString(); //an ID, assigned on receipt
  public String creator; //the user this activity belongs to
  public Date date; //activity date
  public List<Waypoint> waypoints; //the activity data
  public ReductionResult result; //the reduction result

  public Activity(String userID, List<Waypoint> actWpts) {
    creator = userID;
    waypoints = actWpts;
    date = Date.from(actWpts.get(0).timestamp);
  }

  public static Activity deserialize(byte[] data) throws IOException, ClassNotFoundException {
    return (Activity) Util.deserialize(data);
  }
}
