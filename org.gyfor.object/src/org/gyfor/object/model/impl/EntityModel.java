package org.gyfor.object.model.impl;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IValueReference;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;

public class EntityModel extends NameMappedModel implements IEntityModel {
  
  private final IValueReference valueRef;
  
  private final IEntityPlan<?> entityPlan;
  
  
  public EntityModel (ModelFactory modelFactory, IValueReference valueRef, IEntityPlan<?> entityPlan) {
    super (modelFactory, valueRef, entityPlan);
    this.valueRef = valueRef;
    this.entityPlan = entityPlan;
  }
  
  
  @Override
  public void setValue (Object value) {
    valueRef.setValue(value);
    syncValue(null, value);
  }
  
  
  @Override
  public <T> T getValue () {
    return valueRef.getValue();
  }
  
  
  @Override
  public void buildQualifiedNamePart (StringBuilder builder, boolean[] isFirst) {
    builder.append(((IEntityPlan<?>)getPlan()).getClassName());
    builder.append('#');
    isFirst[0] = true;
  }
  
  
  @Override
  public Object newInstance() {
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
}
