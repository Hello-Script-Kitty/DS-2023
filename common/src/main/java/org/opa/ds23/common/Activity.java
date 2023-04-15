package org.opa.ds23.common;

import java.util.List;

/**
 * A Java representation of a GPX file contents
 */
public class Activity {
  private static final long serialVersionUID = -3781403728812799646L;

  public String id;

  public String creator;
  public List<Waypoint> waypoints;
}
