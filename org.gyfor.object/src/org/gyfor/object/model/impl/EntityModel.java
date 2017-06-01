package org.gyfor.object.model.impl;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.model.ref.EntityValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IEntityPlan;


public class EntityModel extends NameMappedModel implements IEntityModel {

  private final IEntityPlan<?> entityPlan;
  
  private Object value;
//  private final NameMappedModel nameMappedModel;
  
  
  public EntityModel (IModelFactory modelFactory, IEntityPlan<?> entityPlan) {
    super (modelFactory, null, null, entityPlan);
    IValueReference valueRef = new EntityValueReference() {
      @Override
      public <T> void setValue(T value) {
        EntityModel.this.value = value;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T> T getValue() {
        return (T)EntityModel.this.value;
      }
    };
    super.setValueReference(valueRef);
    
    this.entityPlan = entityPlan;
  }
    
  
  public EntityModel (IModelFactory modelFactory, IEntityPlan<?> entityPlan, Object instance) {
    this (modelFactory, entityPlan);
    setValue (instance);
  }
    
  
  @Override
  public Object newInstance() {
    return entityPlan.newInstance();
  }
  
//  @Override
//  public void setValue (Object value) {
//    //if (value == null) {
//      //throw new IllegalArgumentException("EntityModel setValue cannot be null");
//    //}
//    nameMappedModel.setValue(value);
//  }
//  
//  
//  @Override
//  public <X> X getValue () {
//    return nameMappedModel.getValue();
//  }
//  
//  
//  @SuppressWarnings("unchecked")
//  @Override
//  public IEntityPlan<?> getPlan() {
//    return nameMappedModel.getPlan();
//  }
//  
//  
//  @Override
//  public String getName() {
//    return nameMappedModel.getName();
//  }
//  
//  
  @Override
  public String toString () {
    return "EntityModel(" + entityPlan.getEntityName() + ")";
  }

  
//  @Override
//  public void dump(int level) {
//    for (int i = 0; i < level; i++) {
//      System.out.print("  ");
//    }
//    System.out.println("EntityModel: " + entityPlan.getName());
//    nameMappedModel.dump(level + 1);
//  }
//
//
//  @Override
//  public <X extends INodeModel> X getMember(String name) {
//    return nameMappedModel.getMember(name);
//  }
//
//
//  @Override
//  public List<INodeModel> getMembers() {
//    return nameMappedModel.getMembers();
//  }
//
//
//  @Override
//  public void addContainerChangeListener(ContainerChangeListener x) {
//    nameMappedModel.addContainerChangeListener(x);
//  }
//
//
//  @Override
//  public void removeContainerChangeListener(ContainerChangeListener x) {
//    nameMappedModel.removeContainerChangeListener(x);
//  }
//
//
//  @Override
//  public void fireChildAdded(IContainerModel parent, INodeModel node, Map<String, Object> context) {
//    nameMappedModel.fireChildAdded(parent, node, context);
//  }
//
//
//  @Override
//  public void fireChildRemoved(IContainerModel parent, INodeModel node) {
//    nameMappedModel.fireChildRemoved(parent, node);
//  }
//
//
//  @Override
//  public IContainerModel getParent() {
//    // The following call will return null
//    return nameMappedModel.getParent();
//  }
//
//
//  @Override
//  public List<INodeModel> getChildNodes() {
//    return nameMappedModel.getChildNodes();
//  }
//
//
//  @Override
//  public int getNodeId() {
//    return nameMappedModel.getNodeId();
//  }
//
//
//  @Override
//  public String getCanonicalName() {
//    return nameMappedModel.getCanonicalName();
//  }
//
//
//  @Override
//  public void setEntryMode(EntryMode entryMode) {
//    nameMappedModel.setEntryMode(entryMode);
//  }
//
//
//  @Override
//  public void updateEffectiveEntryMode(EffectiveEntryMode parent) {
//    nameMappedModel.updateEffectiveEntryMode(parent);
//  }
//
//
//  @Override
//  public EffectiveEntryMode getEffectiveEntryMode() {
//    return nameMappedModel.getEffectiveEntryMode();
//  }
//
//
//  @Override
//  public EntryMode getEntryMode() {
//    return nameMappedModel.getEntryMode();
//  }
//
//
//  @Override
//  public void addEffectiveEntryModeListener(EffectiveEntryModeListener x) {
//    nameMappedModel.addEffectiveEntryModeListener(x);
//  }
//
//
//  @Override
//  public void removeEffectiveEntryModeListener(EffectiveEntryModeListener x) {
//    nameMappedModel.removeEffectiveEntryModeListener(x);
//  }
//
//
//  @Override
//  public void syncEventsWithNode() {
//    throw new NotYetImplementedException();
//    
//  }

}
