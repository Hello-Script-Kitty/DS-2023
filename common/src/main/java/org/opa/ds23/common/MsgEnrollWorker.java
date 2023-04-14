package org.opa.ds23.common;

import java.io.Serializable;

/**
 * The first message sent from a worker to the worker manager (on the master node) in order to enroll as a worker
 * (it's a handshake message)
 */
public class MsgEnrollWorker implements Serializable {
  private static final long serialVersionUID = -1997962114499143819L;
  public String id; //an ID that the worker has constructed for itself
  public int thread_pool; //the number of available threads
}
