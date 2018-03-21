package org.glcore.todo;


public class NotYetImplementedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NotYetImplementedException () {
    super();
  }
  
  public NotYetImplementedException (String msg) {
    super(msg);
  }
  
}
