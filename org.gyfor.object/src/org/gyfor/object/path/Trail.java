package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;

public class Trail<T extends INode> {

  private final Trail<T> parent;
  private final T node;
  
  private boolean visited = false;
  

  public Trail (T node) {
    this.parent = null;
    this.node = node;
  }
  
  Trail (Trail<T> parent, T node) {
    this.parent = parent;
    this.node = node;
  }
  
  void visitAll (Consumer<T> consumer) {
    consumer.accept(node);
    visited = true;
    if (parent != null && !parent.visited) {
      parent.visitAll(consumer);
    }
  }
}

