package org.gyfor.dao;

import java.util.List;

import org.gyfor.object.plan.IEntityPlan;

public interface IDataAccessObject<T> extends AutoCloseable {

  public T getById (int id);
  
  public T getByKey (Object... keyValues);
  
  public boolean existsByKey (Object... keyValues);
  
  public List<T> getAll ();
  
  public List<IdValuePair<String>> getDescriptionAll ();
  
  public void add (T entity);
  
  public void update (T entity);
  
  public void addOrUpdate (T entity);
  
  public void remove (T entity);
  
  @Override
  public void close ();
  
  public IEntityPlan<T> getEntityPlan();
  
}
