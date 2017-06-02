package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.model.INodeModel;
import org.gyfor.object.plan.INodePlan;

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
  public void matches(INodePlan plan, Trail<INodePlan> trail, Consumer<INodePlan> x) {
    if (next != null) {
      next.matches(plan, trail, x);
    } else {
      // We've reached the end of the path
      trail.visitAll(x);
    }
  }

  @Override
  public void matches(INodeModel model, Trail<INodeModel> trail, Consumer<INodeModel> x) {
    if (next != null) {
      next.matches(model, trail, x);
    } else {
      // We've reached the end of the path
      trail.visitAll(x);
    }
  }

}
