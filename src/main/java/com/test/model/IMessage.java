package com.test.model;

public interface IMessage {

  MessageType getMessageType();

  byte[] getHeader();

  byte[] getBody();

  long getMessageId();

  int getMessageLength();

  boolean isEndOfMessage();

}
