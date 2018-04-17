package org.plcore.dao;

public interface IDataAccessObject<T> {

  public static final String EVENT_BASE = "org/plcore/dao/IDataAccessObject/";
  
  public static final String ENTITY_ADDED = "ADDED";
  
  public static final String ENTITY_CHANGED = "CHANGED";
  
  public static final String ENTITY_REMOVED = "REMOVED";
  

  /**
   * Add the instance to the datastore.  The 'value' is updated so  
   * the id, versionTime and entityLife matches the datastore.
   */
  public T add (T value);
  
//  public void addEntityChangeListener (EntityChangeListener<T> x);
//  
//  public void addDescriptionChangeListener (DescriptionChangeListener x);
  
  public void close ();

  public T getById(int id) throws EntityNotFoundException;
  
//  public List<EntityDescription> getDescriptionAll ();
//  
//  public String getDescriptionById (int id);
//  
//  public IEntityPlan<T> getEntityPlan();
//  
//  public T newInstance (T fromValue);

  public void remove (T value) throws ConcurrentModificationException;
  
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
  public T update(T newValue) throws ConcurrentModificationException;
}
