package org.gyfor.berkeleydb;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


public class EntityDAO<E> {

  // Index Accessors
  public final PrimaryIndex<String, E> primaryIndex;
  public final SecondaryIndex<String, String, E> secondaryIndex;

  // Open the indices
  public EntityDAO(EntityStore store, Class<E> entityClass) throws DatabaseException {

    // Primary key for SimpleEntityClass classes
    primaryIndex = store.getPrimaryIndex(String.class, entityClass);

    // Secondary key for SimpleEntityClass classes
    // Last field in the getSecondaryIndex() method must be
    // the name of a class member; in this case, an
    // SimpleEntityClass.class data member.
    secondaryIndex = store.getSecondaryIndex(primaryIndex, String.class, "sKey");
  }

  
  public void put (E entity) {
    primaryIndex.put(entity);
  }
  
  
  public E get (String key) {
    return primaryIndex.get(key);
  }
  
  
  public E getViaSecondary (String key) {
    return secondaryIndex.get(key);
  }
  
  
  public void delete (String key) {
    primaryIndex.delete(key);
  }
  
}
