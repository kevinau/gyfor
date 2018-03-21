package org.plcore.userio.model;

import org.plcore.userio.plan.IEntityPlan;

public interface IModelFactory {

  public IEntityModel buildEntityModel(Class<?> entityClass);

  public IEntityModel buildEntityModel(IEntityPlan<?> entityPlan);

  public IEntityModel buildEntityModel(String entityClassName) throws ClassNotFoundException;

}
