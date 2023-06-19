package org.opa.ds23.gpxr.common.messaging;

import java.io.IOException;
import java.io.Serializable;

/**
 * Carries an activity, usually from the mobile app to the server. Simply transmits a GPX file.
 */
public class ActivityMsg implements Serializable {
  private static final long serialVersionUID = -2207495884028426181L;

  public ActivityMsg() {
  }

  public ActivityMsg(String gpx) {
    gpxContent = gpx;
  }

  public String gpxContent;

  public static ActivityMsg deserialize(byte[] data) throws IOException, ClassNotFoundException {
    return (ActivityMsg) Util.deserialize(data);
  }

  public byte[] serialize() throws IOException {
    return Util.serialize(this);
  }
}
