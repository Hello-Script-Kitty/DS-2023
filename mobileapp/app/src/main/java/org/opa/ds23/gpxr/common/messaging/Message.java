package org.opa.ds23.gpxr.common.messaging;

import java.io.*;

public class Message implements Serializable {
  private static final long serialVersionUID = -3910813626470329656L;

  /**
   * The message type
   */
  public Type type;

  /**
   * The message data. This is a serialized object, that it's type depends on
   */
  public byte[] data;

  public Message(Type type, byte[] data) {
    this.type = type;
    this.data = data;
  }

  public Message(Type type, Serializable o) throws IOException {
    this(type, Util.serialize(o));
  }

  public static Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    return (Message) Util.deserialize(bytes);
  }

  public byte[] serialize() throws IOException {
    return Util.serialize(this);
  }
}
