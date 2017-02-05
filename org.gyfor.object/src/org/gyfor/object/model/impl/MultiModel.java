package org.gyfor.object.model.impl;

import org.gyfor.object.model.INodeModel;
import org.pennyledger.object.model.FormModel;
import org.pennyledger.object.model.NodeModel;
import org.pennyledger.object.plan.INodePlan;

public class MultiModel extends NodeModel {

  private final INodePlan[] nodePlans;
  
  @SuppressWarnings("unused")
  private final Object instance;
  
  private INodeModel[] members;
  
  protected MultiModel(RootModel formModel, INodeModel parent, INodePlan[] nodePlans, Object instance) {
    super(formModel.nextId(), formModel, parent);
    this.nodePlans = nodePlans;
    this.instance = instance;
    members = new INodeModel[nodePlans.length];
  }


  protected void setChildModel (int i, INodeModel model) {
    members[i] = model;
  }


  @Override
  public INodePlan[] getPlan() {
    return nodePlans;
  }
  
  
  @Override
  public String toString () {
    return "MultiModel(" + getId() + ",[" + nodePlans.length + "])";
  }
  
  

}
