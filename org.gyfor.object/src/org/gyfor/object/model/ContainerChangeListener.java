package org.gyfor.object.model;

import java.util.EventListener;

import org.gyfor.object.model.NodeModel;

/**
 * @author Kevin Holloway
 * 
 */
public interface ModelChangeListener extends EventListener {

  /**
   * A node has been added to an container node.
   */
  public void childAdded(NodeModel parent, NodeModel node);

  /**
   * A node has been removed from a container node.
   */
  public void childRemoved(NodeModel parent, NodeModel node);
  
}
