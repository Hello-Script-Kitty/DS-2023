package org.ds23.gpxr.srv;

import org.ds23.gpxr.common.Waypoint;

import java.util.Date;
import java.util.List;

/**
 * Encapsulates single a user activity, as received from the application (whole activity)
 */
public class Activity {
  String id; //an ID, assigned on receipt
  String user; //the user this activity belongs to
  Date date; //activity date
  List<Waypoint> data; //the activity data
}
