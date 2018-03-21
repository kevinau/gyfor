package org.plcore.userio.model;

import java.util.EventListener;

/**
 * @author Kevin Holloway
 * 
 */
public interface EntityCreationListener extends EventListener {

  /**
   * An entity has been created.
   */
  public void entityCreated(IEntityModel node);

  /**
   * An entity has been destroyed.
   */
  public void entityDestoryed(IEntityModel node);
  
}
