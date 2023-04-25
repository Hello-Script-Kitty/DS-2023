package org.opa.ds23.common.messaging;

import java.io.Serializable;

/**
 * Carries an activity, usually from the mobile app to the server. Simply transmits a GPX file.
 */
public class ActivityMsg implements Serializable {
  private static final long serialVersionUID = -2207495884028426181L;

  public String gpxContent;
}
