package org.gyfor.object.model;


public interface IEntityModel extends INameMappedModel {

  public Object newInstance();
  
  public void setValue(Object value);
  
}
