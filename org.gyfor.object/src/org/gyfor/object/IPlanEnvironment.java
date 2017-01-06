package org.gyfor.object;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.impl.AugmentedClass;

public interface IPlanEnvironment {

  public <T> AugmentedClass<T> getClassPlan(Class<T> klass);

  public <T> IEntityPlan<T> getEntityPlan(Class<T> klass);

}
