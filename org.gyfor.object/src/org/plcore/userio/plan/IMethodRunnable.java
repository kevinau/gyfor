package org.plcore.userio.plan;

import org.plcore.userio.UserEntryException;


public interface IMethodRunnable {

  public <T> void run (T instance) throws UserEntryException;
  
}
