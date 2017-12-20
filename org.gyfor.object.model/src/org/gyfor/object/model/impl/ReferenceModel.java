package org.gyfor.object.model.impl;

import org.gyfor.object.model.IReferenceModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IReferencePlan;

public class ReferenceModel extends ItemModel implements IReferenceModel {

  public final IReferencePlan<?> referencePlan;
  
  public ReferenceModel(ModelFactory modelFactory, IValueReference valueRef, IReferencePlan<?> referencePlan) {
    super(modelFactory, valueRef, referencePlan);
    this.referencePlan = referencePlan;
  }

}
