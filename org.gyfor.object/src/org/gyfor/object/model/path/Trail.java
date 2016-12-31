package org.gyfor.object.model.path;

import org.gyfor.object.model.NodeModel;

public class Trail {

  private final Trail parent;
  private final NodeModel nodeModel;
  
  private boolean visited = false;
  

  public Trail (NodeModel nodeModel) {
    this.parent = null;
    this.nodeModel = nodeModel;
  }
  
  Trail (Trail parent, NodeModel nodeModel) {
    this.parent = parent;
    this.nodeModel = nodeModel;
  }
  
  void visitAll (INodeVisitable x) {
    if (parent != null && !parent.visited) {
      parent.visitAll(x);
    }
    visited = true;
    x.visit(nodeModel);
  }
}

