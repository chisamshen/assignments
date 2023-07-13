package com.test.utils;

public class MessageUtil {

  // Should not instantiate util class
  private MessageUtil() {
  }

  private static final ThreadLocal<StringBuilder> reusableStringBuilder = ThreadLocal.withInitial(StringBuilder::new);

  public static String decode(byte[] bytes) {
    var sb = reusableStringBuilder.get();
    sb.setLength(0);
    for (var a : bytes) {
      if (a == 0) {
        break;
      }
      sb.append((char) a);
    }
    return sb.toString();
  }

}
