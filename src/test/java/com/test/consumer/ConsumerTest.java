package com.test.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.test.model.Message;
import com.test.model.MessageFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConsumerTest {

  private Transmission transmission;

  private Consumer classUnderTest;

  private Message message;

  @BeforeEach
  public void beforeEach() {
    transmission = new Transmission();
    classUnderTest = new Consumer(transmission);
    message = MessageFactory.createMessage(0);
    transmission.write(message);
  }

  @Test
  public void when_readMessage_then_consumeMessage() {
    transmission.read(1, classUnderTest);
    assertThat(classUnderTest.getMessageReadCount()).isEqualTo(1);
  }

  @Test
  public void when_readMessages_then_consumeMessages() {
    transmission.write(message);
    assertThatThrownBy(() -> transmission.read(2, classUnderTest)).isInstanceOf(RuntimeException.class);
    assertThat(classUnderTest.getMessageReadCount()).isEqualTo(2);
  }

  @Test
  public void when_noMessageIsRead_then_willNoConsumeMessages() {
    transmission.read(2, classUnderTest);
    assertThat(classUnderTest.getMessageReadCount()).isEqualTo(0);
  }

}
