package org.gyfor.dao;


public class IdValuePair<T extends Comparable<T>> implements Comparable<IdValuePair<T>> {

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


  @Override
  public String toString() {
    return id + ": " + value;
  }


  @Override
  public int compareTo(IdValuePair<T> arg) {
    return value.compareTo(arg.value);
  }
  
}
