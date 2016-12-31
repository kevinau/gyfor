package org.gyfor.object.model.path;

import org.gyfor.object.model.NodeModel;

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
    System.out.println("/  (step path)");
    if (next != null) {
      next.dump(level + 1);
    }
  }
  
  @Override
  public void matches(NodeModel model, Trail trail, INodeVisitable x) {
    if (next != null) {
      next.matches(model, trail, x);
    } else {
      // We've reached the end of the path
      trail.visitAll(x);
    }
  }

//  @Override
//  public boolean matches(NodeModel model, IItemVisitable x) {
//    if (next != null) {
//      return next.matches(model, x);
//    } else {
//      if (model.isItem()) {
//        x.visit((ItemModel)model);
//      }
//      return true;
//    }
//  }

}
