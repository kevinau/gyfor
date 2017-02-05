package org.gyfor.object.model.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.ref.EntityValueReference;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.type.IType;


public class EntityModel extends NameMappedModel implements IEntityModel {

  private final IEntityPlan<?> entityPlan;

  public EntityModel (IEntityPlan<?> entityPlan) {
    super (new AtomicInteger(), null, null, new EntityValueReference(), entityPlan);
    this.entityPlan = entityPlan;
  }
    
  
  public EntityModel (IEntityPlan<?> entityPlan, Object instance) {
    this (entityPlan);
    setValue (instance);
  }
    
  
  @SuppressWarnings("unchecked")
  @Override
  public IEntityPlan<?> getPlan() {
    return entityPlan;
  }
  
  
  @Override
  public String toString () {
    return "EntityModel(" + getNodeId() + "," + entityPlan.getEntityName() + ")";
  }


  @Override
  public void dump(int level) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public IType<?> getType() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public EntryMode getEntryMode() {
    // TODO Auto-generated method stub
    return null;
  }

}
