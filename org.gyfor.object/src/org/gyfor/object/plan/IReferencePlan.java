package org.gyfor.object.plan;


public interface IReferencePlan<T> extends INodePlan {

  public IEntityPlan<T> getReferencedPlan();
  
}
