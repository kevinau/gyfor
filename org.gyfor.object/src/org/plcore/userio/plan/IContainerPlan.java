package org.plcore.userio.plan;

import java.util.List;

import org.plcore.userio.IContainerNode;


public interface IContainerPlan extends INodePlan, IContainerNode {

  public List<INodePlan> selectNodePlans(String expr);

  public List<IItemPlan<?>> selectItemPlans(String expr);

  public INodePlan selectNodePlan(String expr);

  public IItemPlan<?> selectItemPlan(String expr);

  public <X> X newInstance();

}
