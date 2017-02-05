package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;

public abstract class ContainerModel extends NodeModel implements IContainerModel {

  private List<ContainerChangeListener> containerChangeListeners = new ArrayList<>();
  
  
  protected ContainerModel(AtomicInteger idSource, IEntityModel entityModel, IContainerModel parent) {
    super(idSource, entityModel, parent);
  }

  @Override
  public String toString () {
    return "ContainerModel(" + getNodeId() + ")";
  }

  
  @Override
  public void addContainerChangeListener (ContainerChangeListener x) {
    containerChangeListeners.add(x);
  }
  
  @Override
  public void removeContainerChangeListener(ContainerChangeListener x) {
    containerChangeListeners.remove(x);
  }
  
  
  @Override
  public void fireChildAdded(IContainerModel parent, INodeModel node) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childAdded(parent, node);
    }
  }

  @Override
  public void fireChildRemoved(IContainerModel parent, INodeModel node) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childRemoved(parent, node);
    }
  }

}
