package org.gyfor.object.plan;

import java.util.List;


public interface IContainerPlan extends INodePlan {

  public List<INodePlan> selectNodePlans(String expr);

  public List<IItemPlan<?>> selectItemPlans(String expr);

  public INodePlan selectNodePlan(String expr);

  public IItemPlan<?> selectItemPlan(String expr);

}
