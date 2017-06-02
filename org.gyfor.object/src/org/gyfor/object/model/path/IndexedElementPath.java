package org.gyfor.object.model.path;

import java.util.function.Consumer;

import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IRepeatingModel;
import org.gyfor.object.plan.INodePlan;


public class IndexedElementPath extends StepPath implements IPathExpression {

  /**
   * This index is 1 based, to conform with the xpath specification.
   */
  private int index;
  
  public IndexedElementPath (StepPath parent, int index) {
    super(parent);
    this.index = index;
  }
  
  public IndexedElementPath (StepPath parent, String s) {
    this (parent, Integer.parseInt(s));
  }

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("[" + index + "]");
    super.dump(level + 1);
  }

  @Override
  public void matches(INodePlan plan, Trail<INodePlan> trail, Consumer<INodePlan> x) {
    throw new IllegalArgumentException("'indexed element' within a path expression, does not apply to node plans (only node models)");
  }

  
  @Override
  public void matches(INodeModel model, Trail<INodeModel> trail, Consumer<INodeModel> x) {
    if (model instanceof IRepeatingModel) {
      IRepeatingModel repeating = (IRepeatingModel)model;
      INodeModel[] children = repeating.getChildNodes();
      int n = children.length;
      if (index < n) {
        INodeModel member = children[index];
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else {
      throw new IllegalArgumentException("'indexed element' within a path expression, only applies to repeating models");
    }
  }

  
}
