package org.gyfor.dao;

public interface IDataAccessObject {

  public static final String EVENT_BASE = "org/gyfor/doa/IDataAccessObject/";
  
  public static final String ENTITY_ADDED = "ADDED";
  
  public static final String ENTITY_CHANGED = "CHANGED";
  
  public static final String ENTITY_REMOVED = "REMOVED";
  

  /**
   * Add the instance to the datastore.  The 'value' is updated so  
   * the id, versionTime and entityLife matches the datastore.
   */
  public Object add (Object value);
  
//  public void addEntityChangeListener (EntityChangeListener<T> x);
//  
//  public void addDescriptionChangeListener (DescriptionChangeListener x);
  
  public void close ();

  public Object fetchById(Class<?> klass, int id);
  
//  public T getById (int id);
//  
//  public List<EntityDescription> getDescriptionAll ();
//  
//  public String getDescriptionById (int id);
//  
//  public IEntityPlan<T> getEntityPlan();
//  
//  public T newInstance (T fromValue);

  public void remove (Object value) throws ConcurrentModificationException;
  
//  public void removeAll();
//
//  public void removeEntityChangeListener (EntityChangeListener<T> x);
//  
//  public void removeDescriptionChangeListener (DescriptionChangeListener x);
//  
//  /**
//   * In the datastore, sets the entity life field of the instance to RETIRED.  The instance parameter is updated with 
//   * the version and entity life to match the datastore.
//   */
//  public void retire(T instance);
//  
//  /**
//   * In the datastore, sets the entity life field of the instance to ACTIVE.  The instance parameter is updated with 
//   * the version and entity life updated to match the datastore.
//   */
//  public void unretire(T instance);
  
  /**
   * Change the instance in the datastore.  The newInstance parameter is updated with 
   * the version updated to match the datastore.
   */
  public Object update(Object newValue) throws ConcurrentModificationException;
}
