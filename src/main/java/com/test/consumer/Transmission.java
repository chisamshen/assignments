package com.test.consumer;

import com.test.model.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Transmission {

  private final ThreadLocal<List<Message>> reusableMessageList = ThreadLocal.withInitial(() -> new ArrayList<>(10));
  private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();

  interface MessageMuncher {
    boolean onMessage(Message message);
  }

  /**
   *
   * @param howMany
   * @param m
   */
  public void read(int howMany, MessageMuncher m) {
    // TODO - There will be unread residual messages in the queue when the consumer is stopped and the residual messages < 10.
    if (messageQueue.size() < howMany) {
      return;
    }
    var messageList = getReusableMessageList();
    messageQueue.drainTo(messageList, howMany);
    for (var message : messageList) {
      m.onMessage(message);
    }
  }

  public int write(Message message) {
    var messageLength = message.getMessageLength();
    if (messageLength <= 0) {
      throw new IllegalArgumentException("Empty message, messageId: " + message.getMessageId());
    }
    messageQueue.add(message);
    return messageLength;
  }

  public int getMessageQueueSize() {
    return messageQueue.size();
  }

  private List<Message> getReusableMessageList() {
    var list = reusableMessageList.get();
    list.clear();
    return list;
  }

}
