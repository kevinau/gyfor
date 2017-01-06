package org.gyfor.object.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gyfor.object.IPlanEnvironment;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.impl.AugmentedClass;
import org.gyfor.object.plan.impl.EntityPlan;
import org.osgi.service.component.annotations.Component;


@Component
public class PlanEnvironment implements IPlanEnvironment {

  private Map<Class<?>, IEntityPlan<?>> entityPlans = new ConcurrentHashMap<>();
  private Map<Class<?>, AugmentedClass<?>> classPlans = new ConcurrentHashMap<>(20);


  @SuppressWarnings("unchecked")
  public <T> IEntityPlan<T> getEntityPlan(String klassName) {
    Class<T> klass;
    try {
      klass = (Class<T>)Class.forName(klassName);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    return getEntityPlan(klass);
  }


  @Override
  @SuppressWarnings("unchecked")
  public <T> IEntityPlan<T> getEntityPlan(Class<T> klass) {
    IEntityPlan<T> plan = (IEntityPlan<T>)entityPlans.get(klass);
    if (plan == null) {
      plan = new EntityPlan<T>(this, klass);
      entityPlans.put(klass, plan);
    }
    return plan;
  }


  @Override
  @SuppressWarnings("unchecked")
  public <T> AugmentedClass<T> getClassPlan(Class<T> klass) {
    AugmentedClass<T> plan = (AugmentedClass<T>)classPlans.get(klass);
    if (plan == null) {
      plan = new AugmentedClass<T>(this, klass);
      classPlans.put(klass, plan);
    }
    return plan;
  }

}
