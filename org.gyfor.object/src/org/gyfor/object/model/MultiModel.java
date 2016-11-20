package org.gyfor.object.model;

import org.pennyledger.object.model.FormModel;
import org.pennyledger.object.model.NodeModel;
import org.pennyledger.object.plan.INodePlan;

public class MultiModel extends NodeModel {

  private final INodePlan[] nodePlans;
  
  @SuppressWarnings("unused")
  private final Object instance;
  
  private NodeModel[] members;
  
  protected MultiModel(RootModel formModel, NodeModel parent, INodePlan[] nodePlans, Object instance) {
    super(formModel.nextId(), formModel, parent);
    this.nodePlans = nodePlans;
    this.instance = instance;
    members = new NodeModel[nodePlans.length];
  }


  protected void setChildModel (int i, NodeModel model) {
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
