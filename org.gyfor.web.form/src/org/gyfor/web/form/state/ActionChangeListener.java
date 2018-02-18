package org.gyfor.web.form.state;

import java.util.EventListener;

/**
 * @author Kevin Holloway
 * 
 */
public interface ActionChangeListener extends EventListener {

  /**
   * The state of an action has changed.  
   */
  public void actionChanged(Enum<?> action, boolean available);
  
}
