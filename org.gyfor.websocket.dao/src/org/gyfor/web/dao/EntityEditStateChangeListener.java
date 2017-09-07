package org.gyfor.web.dao;

import java.util.EventListener;

public interface EntityEditStateChangeListener extends EventListener {

  /**
   * The entity edit state has changed.
   */
  public void stateChanged (EntityEditState state);
  
}
