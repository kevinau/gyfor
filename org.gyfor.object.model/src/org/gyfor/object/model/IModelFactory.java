package org.gyfor.object.model;

import org.gyfor.object.plan.IEntityPlan;

public interface IModelFactory {

  public IEntityModel buildEntityModel(Class<?> entityClass);

  public IEntityModel buildEntityModel(IEntityPlan<?> entityPlan);

  public IEntityModel buildEntityModel(String entityClassName) throws ClassNotFoundException;

}
