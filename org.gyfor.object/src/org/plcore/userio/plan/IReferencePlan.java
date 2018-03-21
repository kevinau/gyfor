package org.plcore.userio.plan;


public interface IReferencePlan<T> extends IItemPlan<Integer> {

  public IEntityPlan<T> getReferencedPlan();
  
}
