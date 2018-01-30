package org.gyfor.web.form.state;

import java.util.EventListener;

/**
 * @author Kevin Holloway
 * 
 */
public interface OptionChangeListener extends EventListener {

  /**
   * The state of an option has changed.  
   */
  public void optionChanged(Enum<?> option, boolean available);
  
}
