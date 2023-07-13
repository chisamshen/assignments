package com.test.utils;

public interface IObjectPool<T> {

  T take();

  void release(T t);

  int getAvailableCount();

}
