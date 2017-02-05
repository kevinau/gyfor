package org.gyfor.object.model;

import java.util.List;

public interface IContainerModel extends INodeModel {

  public List<INodeModel> getMembers();

  public void addContainerChangeListener(ContainerChangeListener x);
  
  public void removeContainerChangeListener(ContainerChangeListener x);
  
  public void fireChildAdded(IContainerModel parent, INodeModel node);

  public void fireChildRemoved(IContainerModel parent, INodeModel node);

}
