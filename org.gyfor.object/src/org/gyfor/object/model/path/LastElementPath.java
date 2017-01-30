package org.gyfor.object.model.path;

import java.util.List;
import java.util.function.Consumer;

import org.gyfor.object.model.NodeModel;
import org.gyfor.object.model.RepeatingModel;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INameMappedPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.todo.NotYetImplementedException;


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
  public void matches(NodeModel model, Trail<NodeModel> trail, Consumer<NodeModel> x) {
    if (model instanceof RepeatingModel) {
      RepeatingModel repeating = (RepeatingModel)model;
      List<NodeModel> members = repeating.getMembers();
      int n = members.size();
      if (n > 0) {
        NodeModel member = members.get(n - 1);
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else {
      throw new IllegalArgumentException("'last element' within a path expression, only applies to repeating models");
    }
  }

}
