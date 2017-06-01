package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;

import org.gyfor.object.Entity;
import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.ItemLabelGroup;
import org.gyfor.object.plan.PlanStructure;


public class ReferencePlan<T> extends NodePlan implements IReferencePlan<T> {

  private final IEntityPlan<T> referencedPlan;
  private final ReferenceLabelGroup labels;


  public ReferencePlan(IPlanFactory context, INodePlan parent, Field field, Class<T> referencedClass, String pathName, EntryMode entryMode) {
    super(parent, field, pathName, entryMode);
    if (!referencedClass.isAnnotationPresent(Entity.class)) {
      throw new IllegalArgumentException("Referenced class is not annotated with @Entity");
    }
    this.referencedPlan = context.getEntityPlan(referencedClass);
    this.labels = new ReferenceLabelGroup(field, pathName);
  }


  @Override
  public IEntityPlan<T> getReferencedPlan() {
    return referencedPlan;
  }


  @Override
  public INodePlan[] getChildNodes () {
    return new INodePlan[] {
        referencedPlan,
    };
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
