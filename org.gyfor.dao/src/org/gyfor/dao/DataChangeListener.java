package org.gyfor.dao;


public interface DataChangeListener {

  public void entityAdded (int id, Object entity);
  
  public void entityChanged (int id, Object entity);
  
  public void entityRemoved (int id, Object entity);
  
  public void entityRetired (int id);
  
  public void entityUnretired (int id);
  
}
