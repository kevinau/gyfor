package org.gyfor.object.model.path;

import org.gyfor.object.model.NodeModel;
import org.gyfor.object.model.RepeatingModel;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;


public class WildcardPath extends StepPath implements IPathExpression {

  public WildcardPath (StepPath parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("*");
    super.dump(level + 1);
  }

  @Override
  public void matches(INodePlan plan, Trail trail, INodeVisitable x) {
    if (plan instanceof IContainerPlan) {
      IContainerPlan container = (IContainerPlan)plan;
      for (INodePlan member : container.getMembers()) {
        super.matches(member, new Trail(trail, member), x);
      }
    } else if (plan instanceof IRepeatingPlan) {
      RepeatingModel repeating = (RepeatingModel)plan;
      for (INodePlan member : repeating.getElements()) {
        super.matches(member, new Trail(trail, member), x);
      }
    } else if (plan instanceof IItemPlan || plan instanceof IReferencePlan) {
      super.matches(plan, new Trail(trail, plan), x);
    } else {
      throw new RuntimeException("Plan " + plan.getClass().getSimpleName() + " not supported");
    }
  }

  
  private void matchLevel (NodeModel model, Trail trail, INodeVisitable x) {
    if (model.isContainer()) {
      MappedModel container = (MappedModel)model;
      for (NodeModel member : container.getMembers()) {
        super.matches(member, new Trail(trail, member), x);
      }
    } else if (model.isRepeating()) {
      RepeatingModel repeating = (RepeatingModel)model;
      for (NodeModel member : repeating.getElements()) {
        super.matches(member, new Trail(trail, member), x);
      }
    } else if (model.isItem()) {
      super.matches(model, new Trail(trail, model), x);
    } else {
      throw new RuntimeException("Model of type '" + model.getClass().getSimpleName() + "' is not supported");
    }
  }

  //  @Override
//  public boolean matches(IObjectWrapper wrapper, IFieldVisitable x) {
//    for (IObjectWrapper child : wrapper.getChildren()) {
//      super.matches(child, x);
//    }
//    return true;
//  }

}
