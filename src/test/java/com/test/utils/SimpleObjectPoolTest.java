package com.test.utils;

import static com.test.utils.SimpleObjectPool.DEFAULT_CAPACITY;
import static com.test.utils.SimpleObjectPool.DEFAULT_REALLOCATION_FACTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimpleObjectPoolTest {

  private SimpleObjectPool<TestObject> pool;

  @BeforeEach
  public void beforeEach() {
    pool = new SimpleObjectPool<>(TestObject::new, TestObject.class);
  }

  @Test
  public void when_takeFromPool_then_returnObjects() {
    var testObject1 = pool.take();
    var testObject2 = pool.take();
    var testObject3 = pool.take();
    assertThat(testObject1).isNotEqualTo(testObject2);
    assertThat(testObject2).isNotEqualTo(testObject3);
    assertThat(pool.getAvailableCount()).isEqualTo(DEFAULT_CAPACITY - 3);
  }

  @Test
  public void when_takeAndReleaseFromPool_then_objectReset() {
    var testObject1 = pool.take();
    var testObject2 = pool.take();
    testObject1.num = 1;
    testObject1.text = "1";
    testObject2.num = 2;
    testObject2.text = "2";
    assertThat(pool.getAvailableCount()).isEqualTo(DEFAULT_CAPACITY - 2);
    assertThat(testObject1.num).isEqualTo(1);
    assertThat(testObject1.text).isEqualTo("1");
    assertThat(testObject2.num).isEqualTo(2);
    assertThat(testObject2.text).isEqualTo("2");

    pool.release(testObject1);
    pool.release(testObject2);
    assertThat(testObject1.num).isEqualTo(0);
    assertThat(testObject1.text).isNull();
    assertThat(testObject2.num).isEqualTo(0);
    assertThat(testObject2.text).isNull();
    assertThat(pool.getAvailableCount()).isEqualTo(DEFAULT_CAPACITY);
  }

  @Test
  public void when_takeFromEmptyPool_then_triggerPoolResize() {
    int availableItems = pool.getAvailableCount();
    for (int i = 0; i < availableItems; i++) {
      pool.take();
    }
    assertThat(pool.getAvailableCount()).isEqualTo(0);
    var testObject = pool.take();
    assertThat(testObject).isNotNull();
    // 1000 * 0.25 - 1
    assertThat(pool.getAvailableCount()).isEqualTo((int)(DEFAULT_CAPACITY * DEFAULT_REALLOCATION_FACTOR) - 1);
    assertThat(pool.getCapacity()).isEqualTo((int)(DEFAULT_CAPACITY * DEFAULT_REALLOCATION_FACTOR) + DEFAULT_CAPACITY);
  }

  @Test
  public void when_initPoolWithCapacityAndFactor_then_success() {
    pool = new SimpleObjectPool<>(TestObject::new, TestObject.class, 5000, 0.5f);
    assertThat(pool.getReallocationFactor()).isEqualTo(0.5f);
    assertThat(pool.getAvailableCount()).isEqualTo(5000);
  }

  @Test
  public void when_releaseToFullPool_then_fail() {
    var testObject = pool.take();
    pool.release(testObject);
    assertThat(pool.getAvailableCount()).isEqualTo(DEFAULT_CAPACITY);
    pool.release(testObject);
    assertThat(pool.getAvailableCount()).isEqualTo(DEFAULT_CAPACITY);
  }

  @Test
  public void when_releaseSameObjectToPoolTwice_then_fail() {
    pool.take();
    pool.take();
    var testObject = pool.take();
    assertThatNoException().isThrownBy(() -> pool.release(testObject));
    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> pool.release(testObject));
  }

  private static class TestObject extends Resettable {

    int num;
    String text;

    @Override
    public void reset() {
      num = 0;
      text = null;
    }
  }

}
