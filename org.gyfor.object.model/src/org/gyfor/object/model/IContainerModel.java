package org.gyfor.object.model;

import java.util.List;
import java.util.Map;


public interface IContainerModel extends INodeModel {

  public INodeModel[] getMembers();

  public void addContainerChangeListener(ContainerChangeListener x);

  public void removeContainerChangeListener(ContainerChangeListener x);

  public void fireChildAdded(IContainerModel parent, INodeModel node, Map<String, Object> context);

  public void fireChildRemoved(IContainerModel parent, INodeModel node);

  public List<INodeModel> selectNodeModels(String expr);

  public List<IItemModel> selectItemModels(String expr);

  public <X extends INodeModel> X selectNodeModel(String expr);

  public IItemModel selectItemModel(String expr);

}