package org.gyfor.object.model;

import java.util.Collections;
import java.util.List;

import org.gyfor.object.plan.IEntityPlan;

public class EntityModel extends NameMappedModel {

  private final IEntityPlan<?> entityPlan;

  public EntityModel (RootModel rootModel, IEntityPlan<?> entityPlan) {
    this (rootModel, null, rootModel.nextId(), entityPlan);
  }
    
  
  public EntityModel (RootModel rootModel, IEntityPlan<?> entityPlan, Object instance) {
    this (rootModel, null, rootModel.nextId(), entityPlan, instance);
  }
    
  
  public EntityModel (RootModel rootModel, ContainerModel parent, int id, IEntityPlan<?> entityPlan) {
    super (rootModel, parent, id, entityPlan);
    this.entityPlan = entityPlan;
  }
    
  
  public EntityModel (RootModel rootModel, ContainerModel parent, int id, IEntityPlan<?> entityPlan, Object instance) {
    this (rootModel, parent, id, entityPlan);
    setValue (instance);
  }
    
  
  @Override
  public void setValue (Object value) {
    if (value == null) {
      throw new IllegalArgumentException("EntityModel setValue cannot be null");
    }
    super.setValue(value);
  }
  
  
  @Override
  public IEntityPlan<?> getPlan() {
    return entityPlan;
  }
  
  
  @Override
  public String toString () {
    return "EntityModel(" + getId() + "," + entityPlan.getEntityName() + ")";
  }


//  @Override
//  public String toHTML() {
//    int id = getId();
//    String html = "<div id='node-" + id + "'>"
//                + "<span id='node-" + id + "'></span>"
//                + "</div>";
//    return html;
//  }
}
