package org.plcore.userio.model.impl;

import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.ArrayValueReference;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.plan.IArrayPlan;
import org.plcore.userio.plan.INodePlan;

public class ArrayModel extends RepeatingModel {

  private final IArrayPlan arrayPlan;
  
  public ArrayModel (ModelFactory modelFactory, IValueReference valueRef, IArrayPlan arrayPlan) {
    super (modelFactory, valueRef, arrayPlan);
    this.arrayPlan = arrayPlan;
  }

  
  @Override
  public void syncValue(Object value) {
    if (value == null) {
      for (INodeModel element : elements) {
        elements.remove(element);
      }
    } else {
      Object[] arrayValues = (Object[])value;
      int i = 0;
      for (Object arrayValue : arrayValues) {
        INodeModel element;
        if (i < elements.size()) {
          element = elements.get(i);
        } else {
          IValueReference elementValueRef = new ArrayValueReference(valueRef, i);
          element = buildNodeModel(this, elementValueRef, arrayPlan.getElementPlan());
          elements.add(element);
        }
        element.syncValue(arrayValue);
        i++;
      }
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodePlan> X getPlan() {
    return (X)arrayPlan;
  }

}
