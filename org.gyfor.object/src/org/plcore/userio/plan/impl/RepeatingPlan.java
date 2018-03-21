package org.plcore.userio.plan.impl;

import java.util.Collection;
import java.util.Collections;

import org.plcore.userio.EntryMode;
import org.plcore.userio.Occurs;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IRepeatingPlan;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.NodePlanFactory;
import org.plcore.userio.plan.PlanFactory;
import org.plcore.userio.plan.RepeatingLabelGroup;


public abstract class RepeatingPlan extends ContainerPlan implements IRepeatingPlan {

  private final static int DEFAULT_MAX_OCCURS = 10;
  
  private final INodePlan elemPlan;
  private final Class<?> elemClass;
  private final RepeatingLabelGroup labels;

  private final int dimension;
  private final int minOccurs;
  private final int maxOccurs;  
  
  
  public RepeatingPlan (PlanFactory planFactory, MemberValueGetterSetter field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
    super (field, name, entryMode);
    elemPlan = NodePlanFactory.getNodePlan(planFactory, elemClass, field, name, entryMode, dimension + 1, false);
    this.elemClass = elemClass;
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
  public INodePlan getElementPlan () {
    return elemPlan;
  }
  
  
  @Override
  public INodePlan getElementNode () {
    return elemPlan;
  }
  
  
  @Override 
  public Class<?> getElementClass() {
    return elemClass;
  }
  
  
  @Override
  public Collection<INodePlan> getContainerNodes() {
    return Collections.singletonList(elemPlan);
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
    System.out.println("Repeating: " + " [" + minOccurs + "," + maxOccurs + "] dimension " + dimension);
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
  
  
  @Override
  public String toString() {
    return "RepeatingPlan[" + super.toString() + "," + elemPlan + "]";
  }
  
  
//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> fieldPlans) {
//    embeddedPlan.accumulateTopItemPlans(fieldPlans);
//  }
  
}
