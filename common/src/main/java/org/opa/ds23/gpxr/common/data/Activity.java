package org.opa.ds23.gpxr.common.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A Java representation of a GPX file contents
 */
public class Activity implements Serializable {
  @Serial
  private static final long serialVersionUID = -3781403728812799646L;

  public String id; //an ID, assigned on receipt
  public String creator; //the user this activity belongs to
  public Date date; //activity date
  public List<Waypoint> waypoints; //the activity data

  public Activity(String userID, List<Waypoint> actWpts) {

  }
}
