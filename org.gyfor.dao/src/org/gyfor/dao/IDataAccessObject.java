package org.gyfor.dao;

import java.sql.Timestamp;
import java.util.List;

import org.gyfor.object.plan.IEntityPlan;

public interface IDataAccessObject<T> extends AutoCloseable {

  public T getById (int id);
  
  public T getByKey (Object... keyValues);
  
  public boolean existsByKey (Object... keyValues);
  
  public List<T> getAll ();
  
  public List<IdValuePair<String>> getDescriptionAll ();
  
  public DataAddStatus add (T instance);
  
  public Timestamp update (T instance);
  
  public void addOrUpdate (T instance);
  
  public void remove (T instance);
  
  @Override
  public void close ();
  
  public IEntityPlan<T> getEntityPlan();

  public Timestamp retire(int id);
  
  public Timestamp unRetire(int id);
  
}
