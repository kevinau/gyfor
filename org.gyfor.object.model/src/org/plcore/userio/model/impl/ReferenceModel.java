package org.plcore.userio.model.impl;

import org.plcore.userio.model.IReferenceModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.plan.IReferencePlan;

public class ReferenceModel extends ItemModel implements IReferenceModel {

  public final IReferencePlan<?> referencePlan;
  
  public ReferenceModel(ModelFactory modelFactory, IValueReference valueRef, IReferencePlan<?> referencePlan) {
    super(modelFactory, valueRef, referencePlan);
    this.referencePlan = referencePlan;
  }

}
