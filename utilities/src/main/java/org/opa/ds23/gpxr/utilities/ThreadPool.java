package org.opa.ds23.gpxr.utilities;

import java.util.LinkedList;

/**
 * A primitive {@link java.util.concurrent.ThreadPoolExecutor} (no fluff)
 */
public class ThreadPool {
  private final PoolWorker[] threads;
  private final LinkedList<Runnable> taskQueue;
  private volatile boolean terminated = false;

  /**
   * Creates the pool with the specified number of worker threads.
   *
   * @param threadCount The number of worker threads (must be >0)
   */
  public ThreadPool(int threadCount) {
    if (threadCount < 1)
      throw new IllegalArgumentException("Invalid number of threads");
    taskQueue = new LinkedList<>();
    threads = new PoolWorker[threadCount];

    for (int i = 0; i < threadCount; i++) {
      PoolWorker w = new PoolWorker();
//      w.setDaemon(true); //set to "daemon" mode
      threads[i] = w;
      threads[i].start();
    }
  }

  /**
   * Queues a task for execution by the workers
   *
   * @param task The task to execute
   */
  public void submit(Runnable task) {
    //do not accept tasks if terminated
    if (terminated)
      return;
    synchronized (taskQueue) {
      taskQueue.add(task); //add the item in the queue
      taskQueue.notify(); //notify a thread of available item
    }
  }

  /**
   * Waits for all tasks to finish and all workers to terminate
   *
   * @throws InterruptedException In case the current thread is terminated
   */
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

  /**
   * Pool worker. Retrieves available incoming tasks for execution.
   */
  private class PoolWorker extends Thread {

    public void run() {
      Runnable task;

      while (true) {
        synchronized (taskQueue) {
          while (taskQueue.isEmpty()) {
            if (terminated)
              break;
            try {
              taskQueue.wait();
            } catch (InterruptedException e) {
              System.err.println("An error occurred while queue is waiting: " + e.getMessage());
            }
          }
          task = taskQueue.poll(); //will be null sometime after termination
        }
        if (task == null && terminated) {
          break;
        }

        // prevent thread leaks
        try {
          if (task != null) {
            task.run();
          }
        } catch (RuntimeException e) {
          System.err.println("Thread pool is interrupted due to an issue: " + e.getMessage());
        }
      }
    }
  }
}
