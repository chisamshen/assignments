package com.test.producer;

import static com.test.utils.MessageUtil.decode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.consumer.Transmission;
import com.test.model.Message;
import com.test.model.MessageType;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProducerTest {

  @Mock
  private Transmission transmission;
  @InjectMocks
  private Producer classUnderTest;
  @Captor
  private ArgumentCaptor<Message> messageCaptor;

  @BeforeEach
  public void beforeEach() {
    when(transmission.write(any())).thenReturn(5);
  }

  private static Stream<Arguments> producerProducesMessagesTestCase() {
    return Stream.of(
        Arguments.of(0),
        Arguments.of(1),
        Arguments.of(10)
    );
  }

  @ParameterizedTest
  @MethodSource("producerProducesMessagesTestCase")
  public void when_producerProducesMessages_then_messagesAreWritten(int numberOfMessages) {
    for (int i = 0; i < numberOfMessages; i++) {
      classUnderTest.produceMessage();
    }
    verify(transmission, times(numberOfMessages)).write(any());
    assertThat(classUnderTest.getMessageWriteCount()).isEqualTo(numberOfMessages);
  }

  @Test
  public void when_producerProducesMessage_then_verifyMessageContent() {
    classUnderTest.produceMessage();
    verify(transmission).write(messageCaptor.capture());
    var message = messageCaptor.getValue();
    assertThat(message.getMessageType()).isEqualTo(MessageType.TYPE_A);
    assertThat(message.getMessageLength()).isEqualTo("Header12".length() + "MessageBody:0".length());
    assertThat(decode(message.getHeader())).isEqualTo("Header12");
    assertThat(decode(message.getBody())).isEqualTo("MessageBody:0");
    assertThat(message.getMessageId()).isEqualTo(0);
    assertThat(message.isEndOfMessage()).isEqualTo(true);
  }

}
