package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.plan.IClassPlan;
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
    if (plan instanceof IClassPlan) {
      IClassPlan<?> mapped = (IClassPlan<?>)plan;
      INodePlan member = mapped.getMember(name);
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
  public void matches(INodeModel model, Trail<INodeModel> trail, Consumer<INodeModel> x) {
    if (model instanceof INameMappedModel) {
      INameMappedModel mapped = (INameMappedModel)model;
      INodeModel member = mapped.getMember(name);
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
