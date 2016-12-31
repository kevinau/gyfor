package org.gyfor.object.model.path;

import org.gyfor.object.model.NodeModel;


public interface INodeVisitable {

  /**
   * Visit the model node.
   */
  public void visit(NodeModel model);

}
