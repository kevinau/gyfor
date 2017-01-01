package org.gyfor.object.model;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.model.ref.ArrayValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.model.ref.ListValueReference;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IRepeatingPlan;

public class ArrayModel extends ContainerModel {

  private final IValueReference valueRef;
  private final IRepeatingPlan repeatingPlan;
  
  private List<NodeModel> modelElems = new ArrayList<>();
  
  
  protected ArrayModel(RootModel rootModel, ContainerModel parent, int id, IValueReference valueRef, IRepeatingPlan repeatingPlan) {
    super(rootModel, parent, id);
    this.valueRef = valueRef;
    this.repeatingPlan = repeatingPlan;
  }
  
  
  @Override
  public List<NodeModel> getMembers() {
    return modelElems;
  }


  @Override
  public INodePlan getPlan() {
    return repeatingPlan;
  }
  
  
  @Override
  public Object getValue() {
    return valueRef.getValue();
  }


  @Override
  public void setValue(Object instance) {
    valueRef.setValue(instance);
    
    if (instance instanceof Object[]) {
      modelElems = new ArrayList<>();
      Object[] array = (Object[])instance;
      for (int i = 0; i < array.length; i++) {
        NodeModel elemModel = getRoot().buildNodeModel(getParent(), new ArrayValueReference(array, i), repeatingPlan.getElementPlan());
        modelElems.add(elemModel);
      }
    } else {
      throw new IllegalArgumentException("An array is required for an array model");
    }
  }


  @Override
  public String toString () {
    return "ArrayModel(" + getId() + "," + repeatingPlan.getName() + ")";
  }
  
}
