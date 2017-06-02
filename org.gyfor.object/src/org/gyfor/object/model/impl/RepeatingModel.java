package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IRepeatingModel;
import org.gyfor.object.model.IValueReference;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IRepeatingPlan;

public abstract class RepeatingModel extends ContainerModel implements IRepeatingModel {

  protected List<INodeModel> elements = new ArrayList<>();
  
  public RepeatingModel (ModelFactory modelFactory, IValueReference valueRef, IRepeatingPlan repeatingPlan) {
    super (modelFactory, valueRef, repeatingPlan);
  }

  @Override
  public void dump(int level) {
    indent (level);
    System.out.println(this.getClass().getSimpleName() + " " + elements.size() + " elements [");
    int i = 0;
    for (INodeModel element : elements) {
      indent (level);
      System.out.println(i + ":");
      element.dump(level + 1);
      i++;
    }
    indent (level);
    System.out.println("]");
  }
  

  @Override
  public INodeModel[] getMembers() {
    INodeModel[] result = new INodeModel[elements.size()];
    return elements.toArray(result);
  }


  @Override
  public INodeModel[] getChildNodes() {
    return getMembers();
  }

}
