package org.plcore.userio.model;

import org.plcore.userio.IRepeatingNode;
import org.plcore.userio.plan.INodePlan;

public interface IRepeatingModel extends IContainerModel, IRepeatingNode {

  @Override
  public <X extends INodePlan> X getPlan ();

}
