package org.gyfor.object.model;

import java.util.List;

public interface IEntityModel extends INameMappedModel {

  public Object newInstance();
  
  public void setValue(Object value);
  
  public List<INodeModel> getDataModels();
  
}
