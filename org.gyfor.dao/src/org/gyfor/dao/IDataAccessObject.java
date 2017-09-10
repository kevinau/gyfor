package org.gyfor.dao;

import java.util.List;
import org.gyfor.object.plan.IEntityPlan;

@Deprecated
public interface IDataAccessObject<T> extends AutoCloseable {

  public static final String EVENT_BASE = "org/gyfor/doa/IDataAccessObject/";
  
  /**
   * Add the instance to the datastore.  The instance parameter is updated so  
   * the id, version and entity life matches the datastore.
   */
  public void add (T instance);
  
  public void addEntityChangeListener (EntityChangeListener<T> x);
  
  public void addDescriptionChangeListener (DescriptionChangeListener x);
  
  @Override
  public void close ();
  
  public boolean existsUnique(int uniqueIndex, Object[] values, int id);
  
  public List<T> getAll ();
  
  public T getById (int id);
  
  public List<EntityDescription> getDescriptionAll ();
  
  public String getDescriptionById (int id);
  
  public IEntityPlan<T> getEntityPlan();
  
  public T newInstance (T fromValue);

  public void remove (T oldInstance);
  
  public void removeAll();

  public void removeEntityChangeListener (EntityChangeListener<T> x);
  
  public void removeDescriptionChangeListener (DescriptionChangeListener x);
  
  /**
   * In the datastore, sets the entity life field of the instance to RETIRED.  The instance parameter is updated with 
   * the version and entity life to match the datastore.
   */
  public void retire(T instance);
  
  /**
   * In the datastore, sets the entity life field of the instance to ACTIVE.  The instance parameter is updated with 
   * the version and entity life updated to match the datastore.
   */
  public void unretire(T instance);
  
  /**
   * Change the instance in the datastore.  The newInstance parameter is updated with 
   * the version updated to match the datastore.
   */
  public void change (T oldInstance, T newInstance);

}
