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
    this(type, serialize(o));
  }

  private static byte[] serialize(Serializable o) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(o);
    }
    return baos.toByteArray();
  }

  public static Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return (Message) ois.readObject();
    }
  }

  public byte[] serialize() throws IOException {
    return serialize(this);
  }
}
