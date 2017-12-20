package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IRepeatingModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.model.ref.IValueReference;
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
    for (INodeModel element : elements) {
      indent (level);
      System.out.println(element.getName() + ":");
      element.dump(level + 1);
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
  public Collection<INodeModel> getContainerNodes() {
    return elements;
  }

  
  @Override
  public void buildQualifiedNamePart (StringBuilder builder, boolean[] isFirst, int[] repeatCount) {
    repeatCount[0]++;
  }
  

  @Override
  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after) {
    before.accept(this);
    for (INodeModel element : elements) {
      element.walkModel(before, after);
    }
    after.accept(this);
  }
  
  
  @Override
  public int size() {
    return elements.size();
  }


  @Override
  public INodeModel getIndexedNode(int n) {
    return elements.get(n);
  }


  
}
