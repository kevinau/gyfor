package org.gyfor.dao;

import java.util.List;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.value.VersionValue;

public interface IDataAccessObject<T> extends AutoCloseable {

  public T getById (int id);
  
  public List<T> getAll ();
  
  public List<IdValuePair<String>> getDescriptionAll ();
  
  public String getDescriptionById (int id);
  
  public DataAddStatus add (T instance);
  
  public VersionValue update (T oldInstance, T newInstance);
  
  public void removeAll();
  
  public void remove (T oldInstance);
  
  @Override
  public void close ();
  
  public IEntityPlan<T> getEntityPlan();

  public VersionValue retire(int id);
  
  public VersionValue unretire(int id);

  public boolean existsUnique(int uniqueIndex, Object[] values, int id);
  
  public T newInstance (T fromValue);
  
}
