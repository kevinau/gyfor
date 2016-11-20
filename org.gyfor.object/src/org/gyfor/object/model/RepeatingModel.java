package org.gyfor.object.model;

import org.pennyledger.object.model.FormModel;
import org.pennyledger.object.model.NodeModel;
import org.pennyledger.object.plan.IRepeatingPlan;

public class RepeatingModel extends NodeModel {

  private final IRepeatingPlan repeatingPlan;
  
  protected RepeatingModel(RootModel formModel, NodeModel parent, IRepeatingPlan repeatingPlan) {
    super(formModel.nextId(), formModel, parent);
    this.repeatingPlan = repeatingPlan;
  }
  
  
  @Override
  public IRepeatingPlan getPlan() {
    return repeatingPlan;
  }
  
  
  @Override
  public String toString () {
    return "RepeatingModel(" + getId() + "," + repeatingPlan.getName() + ")";
  }
  
  
}
