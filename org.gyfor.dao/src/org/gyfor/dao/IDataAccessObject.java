package org.gyfor.dao;

public interface IDataAccessObject {

  public static final String EVENT_BASE = "org/gyfor/doa/IDataAccessObject/";
  
  public static final String ENTITY_ADDED = "ADDED";
  
  public static final String ENTITY_CHANGED = "CHANGED";
  
  public static final String ENTITY_REMOVED = "REMOVED";
  

  /**
   * Add the instance to the datastore.  The instance parameter is updated so  
   * the id, version and entity life matches the datastore.
   */
  public EntityData add (Object value);
  
//  public void addEntityChangeListener (EntityChangeListener<T> x);
//  
//  public void addDescriptionChangeListener (DescriptionChangeListener x);
  
  public void close ();

  public EntityData fetchById(Class<?> klass, int id);
  
//  public boolean existsUnique(int uniqueIndex, Object[] values, int id);
//  
//  public List<T> getAll ();
//  
//  public T getById (int id);
//  
//  public List<EntityDescription> getDescriptionAll ();
//  
//  public String getDescriptionById (int id);
//  
//  public IEntityPlan<T> getEntityPlan();
//  
//  public T newInstance (T fromValue);

  public void remove (EntityData entityData) throws ConcurrentModificationException;
  
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
//  
//  /**
//   * Change the instance in the datastore.  The newInstance parameter is updated with 
//   * the version updated to match the datastore.
//   */
//  public void change (T oldInstance, T newInstance);
//
  public EntityData change(EntityData entityData, Object value) throws ConcurrentModificationException;
}
