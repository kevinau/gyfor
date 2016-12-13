package org.gyfor.util;


public class RunTimer {

  private final long startTime;
  
  public RunTimer () {
    startTime = System.currentTimeMillis();
  }
  
  
  public void report () {
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println((elapsedTime / 1000.0) + " seconds");
  }
  
  
  public void report (String msg) {
    System.out.print(msg + ": ");
    report();
  }
}
