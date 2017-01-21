package org.gyfor.object.model.path;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ParseException extends Exception {

  private static final long serialVersionUID = 1L;

  private final org.gyfor.object.model.path.parser.ParseException nested;
  
  
  ParseException (org.gyfor.object.model.path.parser.ParseException nested) {
    this.nested = nested;
  }
  

  @Override
  public Throwable fillInStackTrace() {
    return nested.fillInStackTrace();
  }
  

  @Override
  public Throwable getCause() {
    return nested.getCause();
  }
  
  
  @Override
  public String getLocalizedMessage() {
    return nested.getLocalizedMessage();
  }
  
  
  @Override
  public String getMessage() {
    return nested.getMessage();
  }
  
  
  @Override
  public StackTraceElement[] getStackTrace() {
    return nested.getStackTrace();
  }
  

  @Override
  public Throwable initCause(Throwable cause) {
    return nested.initCause(cause);
  }


  @Override
  public void printStackTrace() {
    nested.printStackTrace();
  }


  @Override
  public void printStackTrace(PrintStream s) {
    nested.printStackTrace(s);
  }
  
  
  @Override
  public void printStackTrace(PrintWriter s) {
    nested.printStackTrace(s);
  }


  @Override
  public void setStackTrace(StackTraceElement[] stackTrace) {
    nested.setStackTrace(stackTrace);
  }
  
  
  @Override
  public String toString() {
    return nested.toString();
  }

}
