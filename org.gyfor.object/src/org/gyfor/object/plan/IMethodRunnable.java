package org.gyfor.object.plan;

import org.gyfor.object.UserEntryException;


public interface IMethodRunnable {

  public <T> void run (T instance) throws UserEntryException;
  
}
