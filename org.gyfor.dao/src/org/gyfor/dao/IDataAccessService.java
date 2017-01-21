package org.gyfor.dao;

import java.util.List;

public interface IDataAccessService extends AutoCloseable {

  public <T> T getById (int id);
  
  public <T> T getByKey (Object... keyValues);
  
  public boolean existsByKey (Object... keyValues);
  
  public <T> List<T> getAll ();
  
  public List<IdValuePair<String>> getDescriptionAll ();
  
  public void add (Object entity);
  
  public void update (Object entity);
  
  public void addOrUpdate (Object entity);
  
  public void remove (Object entity);
  
  @Override
  public void close ();
  
}
