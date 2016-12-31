package org.gyfor.object.model;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.model.ref.ArrayValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.model.ref.ListValueReference;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IRepeatingPlan;

public class RepeatingModel extends ContainerModel {

  private final IValueReference valueRef;
  private final IRepeatingPlan repeatingPlan;
  
  private List<NodeModel> modelElems = new ArrayList<>();
  
  
  protected RepeatingModel(RootModel rootModel, ContainerModel parent, int id, IValueReference valueRef, IRepeatingPlan repeatingPlan) {
    super(rootModel, parent, id);
    this.valueRef = valueRef;
    this.repeatingPlan = repeatingPlan;
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
    } else if (instance instanceof List) {
      modelElems = new ArrayList<>();
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>)instance;
      for (int i = 0; i < list.size(); i++) {
        NodeModel elemModel = getRoot().buildNodeModel(getParent(), new ListValueReference(list, i), repeatingPlan.getElementPlan());
        modelElems.add(elemModel);
        i++;
      }
    } else {
      throw new IllegalArgumentException("An array or list is required for a repeating model");
    }
  }


  @Override
  public INodePlan getPlan() {
    return repeatingPlan;
  }
  
  
  @Override
  public String toString () {
    return "RepeatingModel(" + getId() + "," + repeatingPlan.getName() + ")";
  }


  @Override
  public Object getValue() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public List<NodeModel> getMembers() {
    return modelElems;
  }
  
  
}
