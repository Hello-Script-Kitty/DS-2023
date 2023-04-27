package org.opa.ds23.gpxr.common.messaging;

import java.io.Serializable;

/**
 * The message sent from a worker to the server (worker manager on the master node) in order to notify it on useful
 * info, currently: an identification string (can be anything but must be unique among workers) and threads available
 * for processing.
 */
public class WorkerStatusMsg implements Serializable {
  private static final long serialVersionUID = -1997962114499143819L;
  public String id; //an ID that the worker has constructed for itself
  public int thread_pool; //the number of available threads
}
