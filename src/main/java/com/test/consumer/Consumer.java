package com.test.consumer;

import static com.test.utils.MessageUtil.decode;
import static org.apache.logging.log4j.util.Unbox.box;

import com.test.model.Message;
import com.test.model.MessageFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Consumer implements Transmission.MessageMuncher, Runnable {

  private final Transmission transmission;
  @Getter
  @Setter
  private boolean isRunning;
  @Getter
  private int messageReadCount;
  @Getter
  @Setter
  private boolean shouldLogMessage = true;

  public Consumer(Transmission transmission) {
    this.transmission = transmission;
  }

  @Override
  public boolean onMessage(Message message) {
    logMessage(message);
    messageReadCount++;
    MessageFactory.releaseMessage(message);
    return message.isEndOfMessage();
  }

  public void run() {
    isRunning = true;
    while (isRunning) {
      transmission.read(10, this);
    }
  }

  private void logMessage(Message message) {
    if (shouldLogMessage) {
      log.info("Message received: messageType={}, messageId={}, messageLength={}, header={}, body={}, isEnded={}.",
          message.getMessageType(),
          box(message.getMessageId()),
          box(message.getMessageLength()),
          decode(message.getHeader()),
          decode(message.getBody()),
          message.isEndOfMessage());
    }
  }

}
