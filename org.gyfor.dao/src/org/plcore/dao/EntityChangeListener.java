package org.plcore.dao;


@Deprecated
public interface EntityChangeListener<T> {

  public void entityAdded (T entity);
  
  public void entityChanged (T fromEntity, T toEntity);
  
  public void entityRemoved (T entity);
  
  public void entityRetired (T entity);
  
  public void entityUnretired (T entity);
  
}
