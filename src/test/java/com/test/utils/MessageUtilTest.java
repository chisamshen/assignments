package com.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MessageUtilTest {

  private static final String TEST_MESSAGE = "test";

  @Test
  public void when_decodeMessage_then_successfully() {
    var result = MessageUtil.decode(TEST_MESSAGE.getBytes());
    assertThat(result).isEqualTo(TEST_MESSAGE);
  }

}
