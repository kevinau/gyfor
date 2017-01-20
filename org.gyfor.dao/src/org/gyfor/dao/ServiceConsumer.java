package org.gyfor.dao;


@FunctionalInterface
public interface ServiceConsumer<S> {

  public void accept (S arg);
  
}
