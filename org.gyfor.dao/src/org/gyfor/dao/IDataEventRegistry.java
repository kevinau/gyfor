package org.gyfor.dao;


public interface IDataEventRegistry {

  public void addDataChangeListener (DataChangeListener x);
  
  public void removeDataChangeListener (DataChangeListener x);
  
  public void fireEntityAdded (Object entity);
  
  public void fireEntityChanged (Object entity);
  
  public void fireEntityRemoved (Object entity);
  
  public void fireEntityRetired (int id);
  
  public void fireEntityUnretired (int id);
  
}
