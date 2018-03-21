package org.plcore.userio.plan.impl;

import org.plcore.userio.EntryMode;
import org.plcore.userio.plan.EmbeddedLabelGroup;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.PlanStructure;


public class EmbeddedPlan<T> extends NameMappedPlan<T> implements IEmbeddedPlan<T> {

  private EmbeddedLabelGroup labels;
  
  
  public EmbeddedPlan (MemberValueGetterSetter field, Class<T> embeddedClass, String name, EntryMode entryMode) {
    super (field, embeddedClass, name, entryMode);
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
