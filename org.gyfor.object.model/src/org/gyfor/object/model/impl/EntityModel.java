package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.model.EntityCreationListener;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;

public class EntityModel extends NameMappedModel implements IEntityModel {
  
  private final IValueReference valueRef;
  
  private final IEntityPlan<?> entityPlan;

  private final List<EntityCreationListener> entityCreationListeners = new ArrayList<>();

  
  public EntityModel (ModelFactory modelFactory, IValueReference valueRef, IEntityPlan<?> entityPlan) {
    super (modelFactory, valueRef, entityPlan);
    this.valueRef = valueRef;
    this.entityPlan = entityPlan;
  }
  
  
  @Override
  public void addEntityCreationListener(EntityCreationListener x) {
    entityCreationListeners.add(x);
    x.entityCreated(this);
  }

  @Override
  public void setValue (Object value) {
    valueRef.setValue(value);
    syncValue(value);
  }
  
  
  @Override
  public <T> T getValue () {
    return valueRef.getValue();
  }
  
  
  @Override
  public void buildQualifiedNamePart (StringBuilder builder, boolean[] isFirst, int[] repeatCount) {
    builder.append(((IEntityPlan<?>)getPlan()).getClassName());
    builder.append('#');
    isFirst[0] = true;
  }
  
  
  @Override
  public <X> X newInstance() {
    return entityPlan.newInstance();
  }
  

  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("EntityModel {");
    super.dump(level + 1);
    indent (level);
    System.out.println("}");
  }


  @Override
  public List<INodeModel> getDataModels() {
    List<INodePlan> dataPlans = entityPlan.getDataPlans();
    List<INodeModel> dataModels = new ArrayList<>(dataPlans.size());
    
    for (INodePlan dataPlan : dataPlans) {
      INodeModel dataModel = getMember(dataPlan.getName());
      dataModels.add(dataModel);  
    }
    return dataModels;
  }
  
  
  @Override
  public void destroy () {
    for (EntityCreationListener x : entityCreationListeners) {
      x.entityDestoryed(this);
    }
    entityCreationListeners.clear();
  }
  
  
  @Override
  public IEntityModel getParentEntity() {
    return this;
  }
  
  
  @Override
  public void buildQName (StringBuilder builder) {
    System.out.println("Entity model: buildQName: " + builder.toString());
  }
 
}
