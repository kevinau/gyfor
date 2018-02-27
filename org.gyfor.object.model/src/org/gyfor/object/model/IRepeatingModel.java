package org.gyfor.object.model;

import org.gyfor.object.IRepeatingNode;
import org.gyfor.object.plan.INodePlan;

public interface IRepeatingModel extends IContainerModel, IRepeatingNode {

  @Override
  public <X extends INodePlan> X getPlan ();

}
