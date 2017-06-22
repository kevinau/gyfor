package org.gyfor.object.plan;

import java.util.List;

import org.gyfor.object.IContainerNode;


public interface IContainerPlan extends INodePlan, IContainerNode {

  public List<INodePlan> selectNodePlans(String expr);

  public List<IItemPlan<?>> selectItemPlans(String expr);

  public INodePlan selectNodePlan(String expr);

  public IItemPlan<?> selectItemPlan(String expr);

}
