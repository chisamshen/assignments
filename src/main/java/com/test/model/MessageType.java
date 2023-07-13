package com.test.model;

public enum MessageType {

  TYPE_A,
  TYPE_B,
  TYPE_C;

  public static MessageType getMessageType(int value) {
    return MessageType.values()[value % 3];
  }

}
