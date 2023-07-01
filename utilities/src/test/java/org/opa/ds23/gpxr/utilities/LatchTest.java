package org.opa.ds23.gpxr.utilities;

import java.util.Random;
import java.util.concurrent.TimeoutException;

public class LatchTest {

  public static void main(String[] args) throws InterruptedException, TimeoutException {
//    one(1000);
//    many(100);
    inverse();
  }

  //find bug when finishing before we've asked for the data
  private static void inverse() throws InterruptedException, TimeoutException {
    LockingContainer<String> stuff = new LockingContainer<>();
    Thread calculator = getCalculator(stuff, 10);
    calculator.start();
    String result = stuff.waitTillSet();
    System.out.printf("Result: %s%n", result);
  }

  //run many pairs
  private static void many(int count) {
    for (int i = 0; i < count; i++) {
      one(100);
    }
  }

  //run one pair
  private static void one(int cap) {
    LockingContainer<String> stuff = getContainerWithAwaiter();
    Thread calculator = getCalculator(stuff, cap);
    calculator.start();
  }

  private static Thread getCalculator(LockingContainer<String> stuff, int cap) {
    Random r = new Random();
    return new Thread(() -> {
      System.out.println("Calculator started!");
      String s = Integer.toString(r.nextInt(1000));
      try {
        Thread.sleep(r.nextInt(cap));
      } catch (InterruptedException e) {
        System.err.println("Calculator error: " + e.getMessage());
      }
      stuff.set(s);
      System.out.println("Calculator finished");
    });
  }

  private static LockingContainer<String> getContainerWithAwaiter() {
    LockingContainer<String> stuff = new LockingContainer<>();
    Thread awaiter = new Thread(() -> {
      System.out.println("Awaiter started!");
      String result = null;
      try {
        result = stuff.waitTillSet();
      } catch (InterruptedException | TimeoutException e) {
        System.err.println("Awaiter error: " + e.getMessage());
      }
      System.out.printf("Result: %s%n", result);
    });
    awaiter.start();
    return stuff;
  }

}
