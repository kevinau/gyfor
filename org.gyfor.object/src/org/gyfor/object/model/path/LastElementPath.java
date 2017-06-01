package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.model.impl2.INodeModel;
import org.gyfor.object.model.impl2.RepeatingModel;
import org.gyfor.object.plan.INodePlan;


public class LastElementPath extends StepPath implements IPathExpression {

  public LastElementPath (StepPath parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("[last]");
    super.dump(level + 1);
  }

  @Override
  public void matches(INodePlan plan, Trail<INodePlan> trail, Consumer<INodePlan> x) {
    throw new IllegalArgumentException("'last element' within a path expression, does not apply to node plans (only node models)");
  }

  
  @Override
  public void matches(INodeModel model, Trail<INodeModel> trail, Consumer<INodeModel> x) {
    if (model instanceof RepeatingModel) {
      RepeatingModel repeating = (RepeatingModel)model;
      INodeModel[] children = repeating.getChildNodes();
      int n = children.length;
      if (n > 0) {
        INodeModel member = children[n - 1];
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else {
      throw new IllegalArgumentException("'last element' within a path expression, only applies to repeating models");
    }
  }

}
