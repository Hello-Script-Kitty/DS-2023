package org.opa.ds23.gpxr.utilities;

import java.util.Random;

class ThreadPoolTest {

  private static ThreadPool tp;

  public static void main(String[] args) throws InterruptedException {
    testSync(3, 9);
//    testSync(2, 1);
  }

  public static void testSync(int threads, int tasks) throws InterruptedException {
    tp = new ThreadPool(threads);
    for (int i = 0; i < tasks; i++) {
      System.out.println("Submitting task " + i);
      int finalI = i;
      tp.submit(() -> {
        int id = finalI;
        System.out.println("Started task " + id);
        Random r = new Random();
        try {
          Thread.sleep(r.nextInt(5000));
        } catch (InterruptedException e) {
          System.err.println("Error while executing task " + id + ": " + e.getMessage());
        }
        System.out.println("Finished task " + id);
      });
    }
    System.out.println("Waiting for worker threads to exit");
    tp.terminate();
    System.out.println("Finished!");
  }
}
