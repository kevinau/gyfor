package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.model.INodeModel;
import org.gyfor.object.plan.INodePlan;


public interface IPathExpression {

  public void dump(int level);

  public default void dump() {
    dump(0);
  }

  public default void indent(int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }

  public void matches(INodePlan plan, Trail<INodePlan> trail, Consumer<INodePlan> x);
  
  public void matches(INodeModel model, Trail<INodeModel> trail, Consumer<INodeModel> x);

}
