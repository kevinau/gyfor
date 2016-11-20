package org.gyfor.util;

public interface ResettableSupplier<T> {

  public T get();
  
  public ResettableSupplier<T> reset();
  
}
