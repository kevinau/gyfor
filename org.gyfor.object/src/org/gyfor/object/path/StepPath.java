package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;


public class StepPath<T extends INode> implements IPathExpression<T> {

  private final StepPath<T> parent;
  protected IPathExpression<T> next = null;
  
  public StepPath (StepPath<T> parent) {
    this.parent = parent;
    this.parent.next = this;
  }
    
  public StepPath () {
    this.parent = null;
  }
    
  @Override
  public void dump (int level) {
    indent(level);
    System.out.println(".  (step path)");
    if (next != null) {
      next.dump(level + 1);
    }
  }
  

  @Override
  public void matches(T node, Trail<T> trail, Consumer<T> consumer) {
    if (next != null) {
      next.matches(node, trail, consumer);
    } else {
      // We've reached the end of the path
      trail.visitAll(consumer);
    }
  }

}
