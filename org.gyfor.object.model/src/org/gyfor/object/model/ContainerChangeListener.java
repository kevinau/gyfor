package org.gyfor.object.model;

import java.util.EventListener;
import java.util.Map;

/**
 * @author Kevin Holloway
 * 
 */
public interface ContainerChangeListener extends EventListener {

  /**
   * A node has been added to an container node.
   */
  public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> context);

  /**
   * A node has been removed from a container node.
   */
  public void childRemoved(IContainerModel parent, INodeModel node);
  
}
