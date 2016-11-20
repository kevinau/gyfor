package org.gyfor.util;

public class NotYetImplementedException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;

  public NotYetImplementedException () {
  }

  public NotYetImplementedException (String message) {
    super (message);
  }

}
