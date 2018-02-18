package org.gyfor.dao;


public class ConcurrentModificationException extends Exception {

  private static final long serialVersionUID = 1L;

  public ConcurrentModificationException(String message) {
    super(message);
  }
  
}
