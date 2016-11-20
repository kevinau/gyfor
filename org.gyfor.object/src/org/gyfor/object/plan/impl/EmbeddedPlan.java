package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;

import org.gyfor.object.EntryMode;
import org.gyfor.object.IPlanFactory;

public class EmbeddedPlan<T> extends ClassPlan<T> {

  private EmbeddedLabelGroup labels;
  
  public EmbeddedPlan (IPlanFactory context, Field field, Class<T> embeddedClass, String name, EntryMode entryMode) {
    super (context, field, embeddedClass, name, entryMode);
    this.labels = new EmbeddedLabelGroup(field, name);
    System.out.println("EmbeddedPlan... " + super.toString());
  }
  

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("EmbeddedPlan: " + getName());
    super.dump(level + 1);
  }

  
  @Override
  public EmbeddedLabelGroup getLabels () {
    return labels;
  }
  
//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> fieldPlans) {
//    embeddedPlan.accumulateTopItemPlans(fieldPlans);
//  }
  
}