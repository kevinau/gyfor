package org.gyfor.object.model;

import java.util.List;

public abstract class ContainerModel extends NodeModel {

  protected ContainerModel(RootModel rootModel, NodeModel parent, int id) {
    super(rootModel, parent, id);
  }

  public abstract List<NodeModel> getMembers ();
  
  
  @Override
  public String toString () {
    return "ContainerModel(" + getId() + ")";
  }


  @Override
  public boolean isContainer() {
    return true;
  }
  
}
