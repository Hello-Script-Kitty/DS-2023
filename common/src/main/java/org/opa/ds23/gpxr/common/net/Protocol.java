package org.opa.ds23.gpxr.common.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
  public static void send(DataOutputStream os, byte[] data) throws IOException {
    //fixme assemble the message
    int len = data.length;
    os.writeInt(len);
    os.write(data);
  }

  /**
   * Receive a message over an input stream. The message consists of a header and the data. The data is an arbitrary
   * byte array of known length.
   *
   * @param is The input stream
   * @return The data of the message
   */
  public static byte[] receive(DataInputStream is) throws IOException {
    //fixme decompose the message and return the data
    int len = is.readInt();
    byte[] buf = is.readNBytes(len);
    return buf;
  }
}
