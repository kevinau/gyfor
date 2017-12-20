package org.gyfor.object.model.ref;

import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.model.ref.IValueReference;

public class ClassValueReference implements IValueReference {

  private final IValueReference parentRef;
  private final INodePlan nodePlan;
  
  
  public ClassValueReference (IValueReference parentRef, INodePlan nodePlan) {
    this.parentRef = parentRef;
    if (nodePlan == null) {
      throw new IllegalArgumentException("'nodePlan' argument must not be null");
    }
    this.nodePlan = nodePlan;
  }
  
    
  @Override
  public String toString() {
    return "ClassValueReference [" + nodePlan + "]";
  }

   
  @Override
  public <T> void setValue(T value) {
    Object parent = parentRef.getValue();
    nodePlan.setFieldValue(parent, value);
  }


  @Override
  public <T> T getValue() {
    Object parent = parentRef.getValue();
    return nodePlan.getFieldValue(parent);
  }


  @Override
  public String getName() {
    return nodePlan.getName();
  }

}

