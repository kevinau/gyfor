package org.plcore.userio.model;

import java.util.EventListener;

/**
 * @author Kevin Holloway
 * 
 */
public interface ContainerChangeListener extends EventListener {

  /**
   * A node has been added to an container node.
   */
  public void childAdded(IContainerModel parent, INodeModel node);

  /**
   * A node has been removed from a container node.
   */
  public void childRemoved(IContainerModel parent, INodeModel node);
  
}
