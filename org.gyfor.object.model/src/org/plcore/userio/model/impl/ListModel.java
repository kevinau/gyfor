package org.plcore.userio.model.impl;

import java.util.List;

import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.model.ref.ListValueReference;
import org.plcore.userio.plan.IListPlan;

public class ListModel extends RepeatingModel {

  private final IListPlan listPlan;
  
  public ListModel (ModelFactory modelFactory, IValueReference valueRef, IListPlan listPlan) {
    super (modelFactory, valueRef, listPlan);
    this.listPlan = listPlan;
  }

  
  @Override
  public void syncValue(Object value) {
    if (value == null) {
      for (INodeModel element : elements) {
        elements.remove(element);
      }
    } else {
      @SuppressWarnings("unchecked")
      List<Object> listValues = (List<Object>)value;
      int i = 0;
      for (Object listValue : listValues) {
        INodeModel element;
        if (i < elements.size()) {
          element = elements.get(i);
        } else {
          IValueReference elementValueRef = new ListValueReference(valueRef, i);
          element = buildNodeModel(this, elementValueRef, listPlan.getElementPlan());
          elements.add(element);
        }
        element.syncValue(listValue);
        i++;
      }
    }
    
  }

}
