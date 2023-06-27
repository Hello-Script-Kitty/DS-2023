package org.opa.ds23.gpxr.utilities;

/**
 * Instances of this class operate as containers that will block the thread getting their contents until an object is
 * put in the container (by another thread). The instance is initially empty. Only two threads should use this
 * mechanism; one waiting on the lock, the other updating the data.
 * <p>
 * It somewhat resembles a {@link java.util.concurrent.CompletableFuture}
 *
 * @param <T> The contained type
 */
public class LockingContainer<T> {
  private final Object lock = new Object();
  private volatile T data;
  private volatile boolean flag = false;

  /**
   * Will block the calling thread until an object is put in the container.
   *
   * @return The object that was put in the container
   * @throws InterruptedException In case of thread interruption
   */
  public T waitTillSet() throws InterruptedException {
    synchronized (lock) {
      while (!flag) {
        lock.wait();
      }
    }
    return data;
  }

  /**
   * Puts the data in the locker and unblocks any threads that have called {@link #waitTillSet()}
   *
   * @param data The data to
   */
  public void set(T data) {
    synchronized (lock) {
      this.data = data;
      flag = true;
      lock.notifyAll();
    }
  }
}
