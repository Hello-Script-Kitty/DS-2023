package org.ds23.gpxr.common;

import java.io.Serializable;

/**
 * The message sent from a worker to the worker manager in order to enroll as a worker
 */
public class MsgEnrollWorker implements Serializable {
  public String id; //an ID that the worker has constructed
  public String address; //the network address of the worker
  public int port; //the TCP control port for the worker
}
