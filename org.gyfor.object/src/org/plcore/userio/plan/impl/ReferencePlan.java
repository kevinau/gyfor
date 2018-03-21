package org.plcore.userio.plan.impl;

import org.plcore.userio.EntryMode;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IReferencePlan;
import org.plcore.userio.plan.ItemLabelGroup;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.PlanFactory;
import org.plcore.userio.plan.PlanStructure;
import org.plcore.userio.type.builtin.IntegerType;


public class ReferencePlan<T> extends ItemPlan<Integer> implements IReferencePlan<T> {

  private final IEntityPlan<T> referencedPlan;
  private final ReferenceLabelGroup labels;

  
  public ReferencePlan(PlanFactory planFactory, MemberValueGetterSetter field, Class<T> referencedClass, String pathName, EntryMode entryMode) {
    super(field, pathName, entryMode, new IntegerType());
    this.referencedPlan = planFactory.getEntityPlan(referencedClass);
    this.labels = new ReferenceLabelGroup(field, pathName);
  }


  @Override
  public IEntityPlan<T> getReferencedPlan() {
    return referencedPlan;
  }


  @SuppressWarnings("unchecked")
  @Override
  public ItemLabelGroup getLabels () {
    return labels;
  }
  
  
  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("Reference: " + referencedPlan.getEntityName());
  }

  // @Override
  // public void accumulateTopItemPlans(List<IItemPlan<?>> fieldPlans) {
  // referencedPlan.accumulateItemPlans(fieldPlans);
  // }

  // @Override
  // public IObjectModel buildModel(IForm<?> form, IObjectModel parent,
  // IContainerReference container) {
  // return new ReferenceModel(form, parent, container, this);
  // }
  //
  //
  // @Override
  // public Object newValue() {
  // return referencedPlan.getIdField().newValue();
  // }

  
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.REFERENCE;
  }


  @Override
  public <X> X newInstance(X fromValue) {
    return fromValue;
  }

}
