package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.model.NameMappedModel;
import org.gyfor.object.model.NodeModel;
import org.gyfor.object.plan.INameMappedPlan;
import org.gyfor.object.plan.INodePlan;


public class NamedMemberPath extends StepPath implements IPathExpression {

  private final String name;
  
  public NamedMemberPath (StepPath parent, String name) {
    super(parent);
    this.name = name;
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println(name + "  (named member)");
    super.dump(level + 1);
  }

  @Override
  public void matches(INodePlan plan, Trail<INodePlan> trail, Consumer<INodePlan> x) {
    if (plan instanceof INameMappedPlan) {
      INameMappedPlan mapped = (INameMappedPlan)plan;
      INodePlan member = mapped.getMemberPlan(name);
      if (member == null) {
        // Do nothing
      } else {
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else {
      // Do nothing
    }
  }

  
  @Override
  public void matches(NodeModel model, Trail<NodeModel> trail, Consumer<NodeModel> x) {
    if (model instanceof NameMappedModel) {
      NameMappedModel mapped = (NameMappedModel)model;
      NodeModel member = mapped.getMember(name);
      if (member == null) {
        // Do nothing
      } else {
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else {
      // Do nothing
    }
  }

//  @Override
//  public boolean matches(NodeModel nodeModel, INodeVisitable x) {
//    if (nodeModel.isContainer()) {
//      ContainerModel containerModel = (ContainerModel)nodeModel;
//      NodeModel member = containerModel.getMember(name);
//      if (member == null) {
//        return false;
//      } else {
//        x.visit(member);
//        return true;
//      }
//    } else {
//      return false;
//    }
//  }
}
