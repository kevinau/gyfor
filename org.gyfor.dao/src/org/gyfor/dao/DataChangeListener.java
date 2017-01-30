package org.gyfor.dao;


public interface DataChangeListener {

  public void entityAdded (Object entity);
  
  public void entityChanged (Object entity);
  
  public void entityRemoved (Object entity);
  
  public void entityRetired (int id);
  
  public void entityUnretired (int id);
  
}
