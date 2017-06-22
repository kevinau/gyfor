package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;

public class Trail<X extends INode> {

  private final Trail<X> parent;
  private final X node;
  
  private boolean visited = false;
  

  public Trail (X node) {
    this.parent = null;
    this.node = node;
  }
  
  Trail (Trail<X> parent, X node) {
    this.parent = parent;
    this.node = node;
  }
  
  void visitAll (Consumer<X> x) {
    if (parent != null && !parent.visited) {
      parent.visitAll(x);
    }
    visited = true;
    x.accept(node);
  }
}

