package com.test.consumer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransimissionTest {

  @Mock
  private Consumer consumer;
  @Mock
  private Message message;
  private Transmission classUnderTest;


  @BeforeEach
  public void beforeEach() {
    classUnderTest = new Transmission();
    when(message.getMessageLength()).thenReturn(10);
  }

  @Test
  public void when_writeNonEmptyMessage_and_readMessage_then_messageIsRead() {
    classUnderTest.write(message);
    classUnderTest.read(1, consumer);
    verify(consumer).onMessage(message);
  }

  @Test
  public void when_writeEmptyMessage_then_failedToWrite_and_noMessageIsRead() {
    when(message.getMessageLength()).thenReturn(0);
    assertThatThrownBy(() -> classUnderTest.write(message)).isInstanceOf(IllegalArgumentException.class);
    classUnderTest.read(1, consumer);
    verify(consumer, never()).onMessage(message);
  }

  @Test
  public void when_writeNonEmptyMessages_then_readMessage_then_messagesAreRead() {
    classUnderTest.write(message);
    classUnderTest.write(message);
    classUnderTest.read(2, consumer);
    verify(consumer, times(2)).onMessage(message);
  }

  @Test
  public void given_messagesBeingWritten_and_readMessagesMoreThanWritten_then_noMessageIsRead() {
    classUnderTest.write(message);
    classUnderTest.write(message);
    classUnderTest.read(3, consumer);
    verify(consumer, never()).onMessage(message);
  }


}
