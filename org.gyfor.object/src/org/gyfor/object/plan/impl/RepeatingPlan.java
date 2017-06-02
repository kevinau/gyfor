package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;

import org.gyfor.object.EntryMode;
import org.gyfor.object.Occurs;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.object.plan.NodePlanFactory;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.plan.RepeatingLabelGroup;


public abstract class RepeatingPlan extends ContainerPlan implements IRepeatingPlan {

  private final static int DEFAULT_MAX_OCCURS = 10;
  
  private final INodePlan elemPlan;
  private final RepeatingLabelGroup labels;

  private final int dimension;
  private final int minOccurs;
  private final int maxOccurs;  
  
  public RepeatingPlan (PlanFactory planFactory, Field field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
    super (field, name, entryMode);
    elemPlan = NodePlanFactory.getNodePlan(planFactory, elemClass, field, name + "[]", entryMode, dimension + 1, false);
    this.dimension = dimension;
    
    Occurs occursAnn = field.getAnnotation(Occurs.class);
    if (occursAnn != null) {
      int[] minAnn = occursAnn.min();
      if (dimension < minAnn.length) {
        this.minOccurs = minAnn[dimension];
      } else {
        this.minOccurs = 0;
      }
      int[] maxAnn = occursAnn.max();
      if (dimension < maxAnn.length) {
        this.maxOccurs = maxAnn[dimension];
      } else {
        this.maxOccurs = DEFAULT_MAX_OCCURS;
      }
    } else {
      this.minOccurs = 0;
      this.maxOccurs = DEFAULT_MAX_OCCURS;
    }
    
    this.labels = new RepeatingLabelGroup(field, name);
  }
  
  
  @Override 
  public void setParent (INodePlan parent) {
    super.setParent(parent);
    elemPlan.setParent(this);
  }
  

  @Override
  public INodePlan[] getChildNodes () {
    return new INodePlan[] {
        elemPlan,
    };
  }
  
  
  @Override
  public INodePlan getElementPlan () {
    return elemPlan;
  }
  
  
  @Override
  public int getMinOccurs () {
    return minOccurs;
  }
  
  
  @Override
  public int getMaxOccurs () {
    return maxOccurs;
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public RepeatingLabelGroup getLabels () {
    return labels;
  }
  
  @Override
  public void dump (int level) {
    indent(level);
    System.out.println("Repeating: " + " [" + minOccurs + "," + maxOccurs + "]");
    elemPlan.dump(level + 1);
  }


  @Override
  public int getDimension() {
    return dimension;
  }

  
  @Override
  public <X> X newInstance (X fromInstance) {
    return elemPlan.newInstance(fromInstance);
  }
  
//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> fieldPlans) {
//    embeddedPlan.accumulateTopItemPlans(fieldPlans);
//  }
  
}
