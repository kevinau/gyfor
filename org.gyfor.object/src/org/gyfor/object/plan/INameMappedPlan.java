package org.gyfor.object.plan;

public interface INameMappedPlan extends INodePlan {
  
  public INodePlan[] getMemberPlans();

  public INodePlan getMemberPlan (String name);

}
