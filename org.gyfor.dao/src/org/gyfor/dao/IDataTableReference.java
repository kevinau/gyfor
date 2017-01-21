package org.gyfor.dao;


public interface IDataTableReference {

  public IDataAccessService newDataAccessService (boolean readOnly);

  //public void addDataChangeListenr (DataChangeListener x);
  
  //public void removeDataChangeListener (DataChangeListener x);
  
}
