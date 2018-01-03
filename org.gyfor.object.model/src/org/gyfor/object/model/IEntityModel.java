package org.gyfor.object.model;

import java.util.List;

public interface IEntityModel extends INameMappedModel {

  public <X> X newInstance();
  
  public void setValue(Object value);
  
  public List<INodeModel> getDataModels();

  public void addEntityCreationListener(EntityCreationListener x);
  
  public void destroy();
  
}
