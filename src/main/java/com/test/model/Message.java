package com.test.model;

import com.test.utils.Resettable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Message extends Resettable implements IMessage  {

  @Setter
  private MessageType messageType;
  @Setter
  private long messageId;
  @Setter
  private int messageLength;
  @Setter
  private boolean endOfMessage;
  private final byte[] header = new byte[8];
  private final byte[] body = new byte[1000];

  public void setHeader(String header) {
    System.arraycopy(header.getBytes(StandardCharsets.US_ASCII), 0, this.header, 0, header.length());
  }

  public void setBody(String body) {
    Arrays.fill(this.body, (byte) 0);
    System.arraycopy(body.getBytes(StandardCharsets.US_ASCII), 0, this.body, 0, body.length());
  }

  public int getBodyLength() {
    for (int i = 0; i < body.length; i++) {
      if (body[i] == 0) {
        return i;
      }
    }
    return 0;
  }

  @Override
  public void reset() {
    messageType = null;
    messageId = 0;
    messageLength = 0;
    endOfMessage = false;
    Arrays.fill(header, (byte) 0);
    Arrays.fill(body, (byte) 0);
  }
}
