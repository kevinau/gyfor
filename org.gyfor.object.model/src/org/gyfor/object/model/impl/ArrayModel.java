package org.gyfor.object.model.impl;

import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.model.ref.ArrayValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IArrayPlan;
import org.gyfor.object.plan.INodePlan;

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
