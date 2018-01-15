package org.gyfor.object.model;

import java.util.List;


public interface IContainerModel extends INodeModel {

  public INodeModel[] getMembers();

  public void addContainerChangeListener(ContainerChangeListener x);

  public void removeContainerChangeListener(ContainerChangeListener x);

  public void fireChildAdded(IContainerModel parent, INodeModel node);

  public void fireChildRemoved(IContainerModel parent, INodeModel node);

  public List<INodeModel> selectNodeModels(String expr);

  public List<IItemModel> selectItemModels(String expr);

  public <X extends INodeModel> X selectNodeModel(String expr);

  public <X extends INodeModel> X selectNodeModel(int id);

  public IItemModel selectItemModel(String expr);
  
  public void addById(INodeModel node);

  public <X extends INodeModel> X getById(int id);
  
}