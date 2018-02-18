package org.gyfor.berkeleydb;

import java.lang.reflect.Field;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.model.PrimaryKey;


public class SimpleEntityDAO {

  // Only the primary index is used here
  public final PrimaryIndex<Object, Object> primaryIndex;


  public SimpleEntityDAO(EntityStore store, Class<Object> entityClass) throws DatabaseException {
    // Primary key for SimpleEntityClass classes
    Class<Object> pkClass = getPrimaryKeyClass(entityClass);
    
    primaryIndex = store.getPrimaryIndex(pkClass, entityClass);
  }

  
  @SuppressWarnings("unchecked")
  private Class<Object> getPrimaryKeyClass(Class<?> klass) {
    Field[] fields = klass.getDeclaredFields();
    for (Field field : fields) {
      PrimaryKey pkAnn = field.getAnnotation(PrimaryKey.class);
      if (pkAnn != null) {
        return (Class<Object>)field.getType();
      }
    }
    
    Class<?> superKlass = klass.getSuperclass();
    if (superKlass != null) {
      Class<?> pkClass = getPrimaryKeyClass(superKlass);
      if (pkClass != null) {
        return (Class<Object>)pkClass;
      }
    }
    return null;
  }
  
  
  public void put (Object entity) {
    primaryIndex.put(entity);
  }
  
  
  @SuppressWarnings("unchecked")
  public <E> E get (Object key) {
    return (E)primaryIndex.get(key);
  }
  
  
  public void delete (Object key) {
    primaryIndex.delete(key);
  }
  
}
