package org.opa.ds23.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * "Implements" the communication protocol.
 * <p>
 * The initial implementation reads a long int from the "header" of the message and then reads the same number of
 * bytes from the message. After that, the message is assumed to have ended and whatever bytes follow consist of a
 * new message.
 */
public class Protocol {

  /**
   * Send a message over an output stream. The message consists of a header and the data. The data is an arbitrary
   * byte array of known length.
   *
   * @param os The output stream
   * @param data The data to send
   */
  public static void send(DataOutputStream os, byte[] data) {
    //fixme assemble the message
  }

  /**
   * Receive a message over an input stream. The message consists of a header and the data. The data is an arbitrary
   * byte array of known length.
   *
   * @param is The input stream
   * @return The data of the message
   */
  public static byte[] receive(DataInputStream is) {
    //fixme decompose the message and return the data
    return null;
  }
}
