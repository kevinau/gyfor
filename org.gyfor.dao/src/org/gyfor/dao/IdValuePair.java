package org.gyfor.dao;


public class IdValuePair<T> {

  private final int id;
  
  private final T value;
  
  
  public IdValuePair (int id, T value) {
    this.id = id;
    this.value = value;
  }
  
  
  public int getId () {
    return id;
  }
  
  
  public T getValue () {
    return value;
  }
  
}
