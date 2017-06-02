package org.gyfor.object.model;

import java.util.EventListener;


/**
 * @author Kevin Holloway
 * 
 */
public interface EffectiveEntryModeListener extends EventListener {

  /**
   * The effective entry mode of the object model has changed.
   */
  public void effectiveModeChanged (INodeModel model, EffectiveEntryMode priorMode);

}
