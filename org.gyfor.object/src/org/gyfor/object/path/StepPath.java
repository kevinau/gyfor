package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;


public class StepPath implements IPathExpression {

  private final StepPath parent;
  protected IPathExpression next = null;
  
  public StepPath (StepPath parent) {
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
  public void matches(INode node, Trail<INode> trail, Consumer<INode> x) {
    if (next != null) {
      next.matches(node, trail, x);
    } else {
      // We've reached the end of the path
      trail.visitAll(x);
    }
  }

}
