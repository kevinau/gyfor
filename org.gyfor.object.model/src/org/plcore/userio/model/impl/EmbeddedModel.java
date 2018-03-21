package org.plcore.userio.model.impl;

import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.INodePlan;

public class EmbeddedModel extends NameMappedModel {
  
  private final IEmbeddedPlan<?> embeddedPlan;
  
  
  public EmbeddedModel (ModelFactory modelFactory, IValueReference valueRef, IEmbeddedPlan<?> embeddedPlan) {
    super (modelFactory, valueRef, embeddedPlan);
    this.embeddedPlan = embeddedPlan;
  }
  
  
  public void setValue (Object value) {
    syncValue(value);
  }
  
  
  @Override
  public void dump() {
    System.out.println("EmbeddedModel:");
    super.dump();
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodePlan> X getPlan() {
    return (X)embeddedPlan;
  }


  @Override
  public void buildQualifiedNamePart (StringBuilder builder, boolean[] isFirst, int[] repeatCount) {
    if (isFirst[0] == false) {
      builder.append('.');
    }
    builder.append(getName());
    isFirst[0] = false;
    for (int i = 0; i < repeatCount[0]; i++) {
      builder.append("[]");
    }
    repeatCount[0] = 0;
  }

}
