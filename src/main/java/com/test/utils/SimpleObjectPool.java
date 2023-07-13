package com.test.utils;

import static org.apache.logging.log4j.util.Unbox.box;

import java.util.ArrayDeque;
import java.util.Deque;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Supplier;

@Log4j2
public class SimpleObjectPool<T extends Resettable> implements IObjectPool<T> {

  public static final int DEFAULT_CAPACITY = 1000;
  public static final float DEFAULT_REALLOCATION_FACTOR = 0.25f;
  private final String objectType;
  @Getter
  private int capacity;
  @Getter
  private final float reallocationFactor;

  private final Supplier<T> supplier;
  private final Deque<T> pool;

  public SimpleObjectPool(Supplier<T> supplier, Class<T> objectType) {
    this(supplier, objectType, DEFAULT_CAPACITY, DEFAULT_REALLOCATION_FACTOR);
  }

  public SimpleObjectPool(Supplier<T> supplier, Class<T> objectType, int capacity, float reallocationFactor) {
    this.capacity = capacity;
    this.reallocationFactor = reallocationFactor;
    this.pool = new ArrayDeque<>(capacity);
    this.supplier = supplier;
    this.objectType = objectType.getSimpleName();
    initPool();
  }

  private void initPool() {
    log.info("Initializing ObjectPool with {} items, objectType: {}", box(capacity), objectType);
    for (int i = 1; i <= capacity; i++) {
      T item = supplier.get();
      item.reset();
      item.setInPool(true);
      pool.add(item);
    }
  }

  @Override
  public synchronized T take() {
    if (pool.isEmpty()) {
      expandPool();
    }
    T item = pool.removeFirst();
    item.setInPool(false);
    return item;
  }

  @Override
  public synchronized void release(T item) {
    if (getAvailableCount() >= capacity) {
      log.error("Failed to release item: {} to full pool[{}]", item, objectType);
    } else {
      if (item.isInPool()) {
        throw new RuntimeException("Failed to release item which is already in pool: " + item);
      } else {
        item.reset();
        item.setInPool(true);
        pool.addLast(item);
      }
    }
  }

  @Override
  public int getAvailableCount() {
    return pool.size();
  }

  private void expandPool() {
    var expandSize = (int) (capacity * reallocationFactor);
    for (int i = 1; i <= expandSize; i++) {
      T item = supplier.get();
      item.reset();
      item.setInPool(true);
      pool.add(item);
    }
    capacity = capacity + expandSize;
    log.warn("Pool[{}] exhausted, expand it to: {}", objectType, box(capacity));
  }

}
