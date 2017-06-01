package org.gyfor.object;

import java.util.HashMap;
import java.util.Map;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.object.plan.impl.EntityPlan;

public class EntityPlanFactory {

  private static Map<Class<?>, IEntityPlan<?>> planCache = new HashMap<>(20);
  
  @SuppressWarnings("unchecked")
  public static <T> IEntityPlan<T> getEntityPlan (IPlanFactory context, Class<T> klass) {
    IEntityPlan<T> plan = (IEntityPlan<T>)planCache.get(klass);
    if (plan == null) {
      plan = new EntityPlan<T>(context, klass);
      planCache.put(klass, plan);
    }
    return plan;
  }
  
  
  @SuppressWarnings("unchecked")
  public static <T> IEntityPlan<T> getEntityPlan (IPlanFactory context, T value) {
    return (IEntityPlan<T>)getEntityPlan(context, value.getClass());
  }
  
}
