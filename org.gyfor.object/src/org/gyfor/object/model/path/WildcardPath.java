package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IRepeatingModel;
import org.gyfor.object.model.impl.ItemModel;
import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.todo.NotYetImplementedException;


public class WildcardPath extends StepPath implements IPathExpression {

  public WildcardPath (StepPath parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("*  (wildcard path)");
    super.dump(level + 1);
  }

  @Override
  public void matches(INodePlan plan, Trail<INodePlan> trail, Consumer<INodePlan> x) {
    if (plan instanceof IClassPlan) {
      IClassPlan<?> mapped = (IClassPlan<?>)plan;
      for (INodePlan member : mapped.getMembers()) {
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else if (plan instanceof IRepeatingPlan) {
      IRepeatingPlan repeating = (IRepeatingPlan)plan;
      INodePlan element = repeating.getElementPlan();
      super.matches(element, new Trail<>(trail, element), x);
    } else if (plan instanceof IItemPlan || plan instanceof IReferencePlan) {
      super.matches(plan, new Trail<>(trail, plan), x);
    } else {
      throw new NotYetImplementedException("Plan " + plan.getClass().getSimpleName());
    }
  }

  
  @Override
  public void matches(INodeModel model, Trail<INodeModel> trail, Consumer<INodeModel> x) {
    if (model instanceof IContainerModel) {
      IContainerModel container = (IContainerModel)model;
      for (INodeModel member : container.getMembers()) {
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else if (model instanceof IRepeatingModel) {
      IRepeatingModel repeating = (IRepeatingModel)model;
      for (INodeModel member : repeating.getMembers()) {
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else if (model instanceof ItemModel) {
      super.matches(model, new Trail<>(trail, model), x);
    } else {
      throw new NotYetImplementedException("Model " + model.getClass().getSimpleName());
    }
  }

  
//  private void matchLevel (NodeModel model, Trail trail, INodeVisitable x) {
//    if (model.isContainer()) {
//      MappedModel container = (MappedModel)model;
//      for (NodeModel member : container.getMembers()) {
//        super.matches(member, new Trail(trail, member), x);
//      }
//    } else if (model.isRepeating()) {
//      RepeatingModel repeating = (RepeatingModel)model;
//      for (NodeModel member : repeating.getElements()) {
//        super.matches(member, new Trail(trail, member), x);
//      }
//    } else if (model.isItem()) {
//      super.matches(model, new Trail(trail, model), x);
//    } else {
//      throw new RuntimeException("Model of type '" + model.getClass().getSimpleName() + "' is not supported");
//    }
//  }

  //  @Override
//  public boolean matches(IObjectWrapper wrapper, IFieldVisitable x) {
//    for (IObjectWrapper child : wrapper.getChildren()) {
//      super.matches(child, x);
//    }
//    return true;
//  }

}
