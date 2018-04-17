package org.plcore.dao;


@Deprecated
public class EntityChangeAdapter<T> implements EntityChangeListener<T> {

  @Override
  public void entityAdded(T entity) {
  }

  @Override
  public void entityChanged(T fromEntity, T toEntity) {
  }

  @Override
  public void entityRemoved(T entity) {
  }

  @Override
  public void entityRetired(T entity) {
  }

  @Override
  public void entityUnretired(T entity) {
  }

}
