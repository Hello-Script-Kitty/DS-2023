package org.opa.ds23.gpxr.utilities;

import java.util.LinkedList;

public class ThreadPool {
  private final PoolWorker[] threads;
  private final LinkedList<Runnable> taskQueue;
  private volatile boolean terminated = false;

  public ThreadPool(int threadCount) {
    taskQueue = new LinkedList<>();
    threads = new PoolWorker[threadCount];

    for (int i = 0; i < threadCount; i++) {
      PoolWorker w = new PoolWorker();
//      w.setDaemon(true); //set to "daemon" mode
      threads[i] = w;
      threads[i].start();
    }
  }

  public void submit(Runnable task) {
    //do not accept tasks if terminated
    if (terminated)
      return;
    synchronized (taskQueue) {
      taskQueue.add(task); //add the item in the queue
      taskQueue.notify(); //notify a thread of available item
    }
  }

  public void terminate() throws InterruptedException {
    terminated = true; //raise termination flag
    //notify all threads (wake them up)
    synchronized (taskQueue) {
      taskQueue.notifyAll();
    }
    //wait for all threads to terminate
    for (Thread t : threads) {
      t.join();
    }
  }

  private class PoolWorker extends Thread {

    private String name;

    private void msg(String msg) {
      System.out.printf("Worker %s says: \"%s\"%n", name, msg);
    }

    public void run() {
      name = Thread.currentThread().getName();
//      msg("starting");
      Runnable task;

      while (true) {
//        msg("entering sync block");
//        msg("t=" + terminated);
        synchronized (taskQueue) {
//          msg("entering poll/wait loop");
          while (taskQueue.isEmpty()) {
            if (terminated)
              break;
//            msg("t=" + terminated);
            try {
              taskQueue.wait();
//              msg("going woke");
//              msg("t=" + terminated);
            } catch (InterruptedException e) {
              System.err.println("An error occurred while queue is waiting: " + e.getMessage());
            }
          }
          task = taskQueue.poll(); //will be null sometime after termination
//          msg(task != null ? "got task" : "null task");
//          msg("t=" + terminated);
        }
        if (task == null && terminated) {
//          msg("no task and 'terminated'... exiting");
          break;
        }

        // prevent thread leaks
        try {
          if (task != null) {
//            msg("running task");
//            msg("t=" + terminated);
            task.run();
          }
        } catch (RuntimeException e) {
          System.err.println("Thread pool is interrupted due to an issue: " + e.getMessage());
        }
      }
//      msg("finished");
    }
  }
}
