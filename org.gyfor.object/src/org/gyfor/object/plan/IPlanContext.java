package org.gyfor.object.plan;

import org.gyfor.object.plan.impl.AugmentedClass;

public interface IPlanContext {

  // TODO change AugentedClass to IAugmentedClass
  public <T> AugmentedClass<T> getClassPlan(Class<T> klass);

  public <T> IEntityPlan<T> getEntityPlan(String className);

  public <T> IEntityPlan<T> getEntityPlan(Class<T> klass);

}
