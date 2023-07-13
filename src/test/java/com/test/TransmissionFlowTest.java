package com.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.test.consumer.Consumer;
import com.test.consumer.Transmission;
import com.test.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransmissionFlowTest {

  private Transmission classUnderTest;

  @BeforeEach
  public void beforeEach() {
    classUnderTest = new Transmission();
  }

  @Test
  public void given_consumerLogMessage_when_startConsumerAndProducerAsync_then_slowConsumer() {
    var consumer = new Consumer(classUnderTest);
    var producer = new Producer(classUnderTest);
    var consumerThread = new Thread(consumer);
    var producerThread = new Thread(producer);
    consumerThread.start();
    producerThread.start();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    producer.setRunning(false);
    consumer.setRunning(false);
    assertThat(producer.getMessageWriteCount()).isPositive();
    assertThat(consumer.getMessageReadCount()).isPositive();
    // Slow consumer will have more than 10 residual messages
    assertThat(producer.getMessageWriteCount() - consumer.getMessageReadCount()).isGreaterThan(10);
  }

  @Test
  public void given_consumerNotLogMessage_when_startConsumerAndProducerAsync_then_noSlowConsumer() {
    var consumer = new Consumer(classUnderTest);
    var producer = new Producer(classUnderTest);
    consumer.setShouldLogMessage(false);
    var consumerThread = new Thread(consumer);
    var producerThread = new Thread(producer);
    consumerThread.start();
    producerThread.start();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    producer.setRunning(false);
    consumer.setRunning(false);
    assertThat(producer.getMessageWriteCount()).isPositive();
    assertThat(consumer.getMessageReadCount()).isPositive();
    // Residual messages should be less than 10
    assertThat(producer.getMessageWriteCount() - consumer.getMessageReadCount()).isLessThan(10);
  }

  @Test
  public void given_throttledProducer_when_startConsumerAndProducerAsync_then_noSlowConsumer() {
    var consumer = new Consumer(classUnderTest);
    var producer = new Producer(classUnderTest);
    producer.setThrottle(true);
    var consumerThread = new Thread(consumer);
    var producerThread = new Thread(producer);
    consumerThread.start();
    producerThread.start();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    producer.setRunning(false);
    consumer.setRunning(false);
    assertThat(producer.getMessageWriteCount()).isPositive();
    assertThat(consumer.getMessageReadCount()).isPositive();
    // Residual messages should be less than 10
    assertThat(producer.getMessageWriteCount() - consumer.getMessageReadCount()).isLessThan(10);
  }
  
  @Test
  public void given_multiConsumers_when_startConsumersAndProducerAsync_then_noSlowConsumer() {
    var consumer1 = new Consumer(classUnderTest);
    var consumer2 = new Consumer(classUnderTest);
    var producer = new Producer(classUnderTest);
    var consumerThread1 = new Thread(consumer1);
    var consumerThread2 = new Thread(consumer2);
    var producerThread = new Thread(producer);
    consumerThread1.start();
    consumerThread2.start();
    producerThread.start();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    producer.setRunning(false);
    consumer1.setRunning(false);
    consumer2.setRunning(false);
    assertThat(producer.getMessageWriteCount()).isPositive();
    assertThat(consumer1.getMessageReadCount()).isPositive();
    assertThat(consumer2.getMessageReadCount()).isPositive();
    // Residual messages should be less than 10
    assertThat(producer.getMessageWriteCount() - consumer1.getMessageReadCount() - consumer2.getMessageReadCount()).isLessThan(10);
  }

}
