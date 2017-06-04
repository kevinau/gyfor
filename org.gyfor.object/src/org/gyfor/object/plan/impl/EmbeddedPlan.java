package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;

import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.EmbeddedLabelGroup;
import org.gyfor.object.plan.IEmbeddedPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.plan.PlanStructure;

public class EmbeddedPlan<T> extends ClassPlan<T> implements IEmbeddedPlan<T> {

  private EmbeddedLabelGroup labels;
  
  
  public EmbeddedPlan (PlanFactory planFactory, Field field, Class<T> embeddedClass, String name, EntryMode entryMode) {
    super (planFactory, field, embeddedClass, name, entryMode);
    this.labels = new EmbeddedLabelGroup(field, name);
  }
  

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("EmbeddedPlan: " + getName());
    super.dump(level + 1);
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public EmbeddedLabelGroup getLabels () {
    return labels;
  }
  
//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> fieldPlans) {
//    embeddedPlan.accumulateTopItemPlans(fieldPlans);
//  }
  
  
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.EMBEDDED;
  }

}
