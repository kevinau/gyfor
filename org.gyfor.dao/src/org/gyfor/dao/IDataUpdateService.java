package org.gyfor.dao;

public interface IDataUpdateService extends IDataFetchService {

  public void add (Object entity);
  
  public void update (int id, Object entity);
  
  public void put (Object entity);
  
  public void remove (int id);
  
  public void addDataChangeListener (DataChangeListener x);
  
  public void removeDataChangeListener (DataChangeListener x);
  
}
