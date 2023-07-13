package com.test.utils;

public abstract class Resettable implements IResettable {

  private boolean isInPool;

  boolean isInPool() {
    return isInPool;
  }

  void setInPool(boolean inPool) {
    isInPool = inPool;
  }
}
