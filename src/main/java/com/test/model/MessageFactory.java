package com.test.model;

import com.test.utils.IObjectPool;
import com.test.utils.SimpleObjectPool;

public class MessageFactory {

  private final static IObjectPool<Message> MESSAGE_POOL = new SimpleObjectPool<>(Message::new, Message.class);

  public static Message createMessage(int id) {
    var message = MESSAGE_POOL.take();
    message.setMessageType(MessageType.getMessageType(id));
    message.setMessageId(id);
    message.setHeader("Header12");
    message.setBody("MessageBody:" + id);
    message.setMessageLength(message.getHeader().length + message.getBodyLength());
    message.setEndOfMessage(true);
    return message;
  }

  public static void releaseMessage(Message message) {
    MESSAGE_POOL.release(message);
  }

}
