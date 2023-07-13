package com.test.producer;

import com.test.consumer.Transmission;
import com.test.model.MessageFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Producer implements Runnable {

  private final Transmission transmission;
  private int sequencer;
  @Getter
  @Setter
  private boolean isRunning;
  @Getter
  @Setter
  private boolean isThrottle = false;
  @Getter
  private int threshold = 50;
  @Getter
  private int messageWriteCount;

  public Producer(Transmission transmission) {
    this.transmission = transmission;
  }

  public void produceMessage() {
    var message = MessageFactory.createMessage(sequencer++);
    var result = transmission.write(message);
    if (result > 0) {
      messageWriteCount++;
    } else {
      log.error("Failed to write message: {}", message);
    }
  }

  @Override
  public void run() {
    isRunning = true;
    while (isRunning) {
      if (isThrottle) {
        if (transmission.getMessageQueueSize() < threshold) {
          produceMessage();
        } else {
          try {
            Thread.sleep(5);
          } catch (InterruptedException e) {
            log.error("Producer thread interrupted.", e);
          }
        }
      } else {
        produceMessage();
      }
    }
  }
}
