package org.gyfor.dao;


@FunctionalInterface
public interface TriServiceConsumer<S, T, R> {

  public void accept (S arg1, T arg2, R arg3);
  
}
