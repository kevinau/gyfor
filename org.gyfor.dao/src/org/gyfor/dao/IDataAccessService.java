package org.gyfor.dao;

import java.util.List;

public interface IDataAccessService extends AutoCloseable {

  public <T> T getById (int id);
  
  public <T> T getByKey (Object... keyValues);
  
  public boolean existsByKey (Object... keyValues);
  
  public <T> List<T> getAll ();
  
  public List<IdValuePair<String>> getDescriptionAll ();
  
  public void add (Object entity);
  
  public void update (int id, Object entity);
  
  public void put (Object entity);
  
  public void remove (int id);
  
  public void addDataChangeListener (DataChangeListener x);
  
  public void removeDataChangeListener (DataChangeListener x);
  
  @Override
  public void close ();
  
}
