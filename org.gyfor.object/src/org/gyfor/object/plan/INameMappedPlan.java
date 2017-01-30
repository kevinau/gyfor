package org.gyfor.object.plan;


public interface INameMappedPlan extends IContainerPlan {
  
  public INodePlan[] getMemberPlans();

  public INodePlan getMemberPlan (String name);

}
