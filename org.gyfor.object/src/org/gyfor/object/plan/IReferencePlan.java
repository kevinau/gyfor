package org.gyfor.object.plan;


public interface IReferencePlan<T> extends IItemPlan<Integer> {

  public IEntityPlan<T> getReferencedPlan();
  
}
