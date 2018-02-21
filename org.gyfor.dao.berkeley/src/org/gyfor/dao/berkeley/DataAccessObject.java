package org.gyfor.dao.berkeley;

import java.lang.reflect.Field;

import org.gyfor.dao.ConcurrentModificationException;
import org.gyfor.dao.IDataAccessObject;
import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.PrimaryIndex;


@Component(property = "type=berkeley")
public class DataAccessObject implements IDataAccessObject {

  private final Logger logger = LoggerFactory.getLogger(DataAccessObject.class);
  
  @Reference 
  private DataStore dataStore;

  @SuppressWarnings("unchecked")
  @Override
  public Object add(Object value) {
    Class<?> klass = value.getClass();
    
    // TODO Should the primary and secondary indexes be cached?
    PrimaryIndex<Integer, Object> primaryById = (PrimaryIndex<Integer, Object>)dataStore.getPrimaryIndex(Integer.class, klass);

    try {
      Field timeField = klass.getDeclaredField("versionTime");
      VersionTime versionTime = VersionTime.now();
      timeField.setAccessible(true);
      timeField.set(value, versionTime);
      
      Field lifeField = klass.getDeclaredField("entityLife");
      lifeField.setAccessible(true);
      lifeField.set(value, EntityLife.ACTIVE);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    
    // Put it in the store. Because we do not explicitly set
    // a transaction here, and because the store was opened
    // with transactional support, auto commit is used for each
    // write to the store.
    primaryById.put(value);
    return value;
  }

  
  @Override
  public void close() {
    // Nothing to do for Berkeley database
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public Object fetchById(Class<?> klass, int id) {
    PrimaryIndex<Integer, Object> primaryById = (PrimaryIndex<Integer, Object>)dataStore.getPrimaryIndex(Integer.class, klass);
    Object value = primaryById.get(id);
    return value;
  }
  

  @SuppressWarnings("unchecked")
  @Override
  public void remove(Object value) throws ConcurrentModificationException {
    Transaction transaction = dataStore.beginTransaction();
    try {
      Class<?> klass = value.getClass();
      Field idField = klass.getDeclaredField("id");
      Field versionField = klass.getDeclaredField("versionTime");
      idField.setAccessible(true);
      int id = idField.getInt(value);
      versionField.setAccessible(true);
      VersionTime oldTime = (VersionTime)versionField.get(value);

      PrimaryIndex<Integer, Object> primaryById = (PrimaryIndex<Integer, Object>)dataStore.getPrimaryIndex(Integer.class, klass);
      Object value2 = primaryById.get(id);
      VersionTime newTime = (VersionTime)versionField.get(value2);
      if (!oldTime.equals(newTime)) {
        throw new ConcurrentModificationException(oldTime + " vs " + newTime);
      }
      primaryById.delete(id);
      transaction.commit();
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException |
             IllegalAccessException | DatabaseException ex) {
      transaction.abort();
      throw new RuntimeException(ex);
    }
  }

  
  /**
   * Change an entity.  Only the 'id' and 'versionTime' of the old value is used.
   */
  @SuppressWarnings("unchecked")
  @Override
  public Object update(Object newValue) throws ConcurrentModificationException {
    Transaction transaction = dataStore.beginTransaction();
    try {
      Class<?> klass = newValue.getClass();
      Field idField = klass.getDeclaredField("id");
      Field versionField = klass.getDeclaredField("versionTime");
      idField.setAccessible(true);
      int id = idField.getInt(newValue);
      versionField.setAccessible(true);
      VersionTime oldTime = (VersionTime)versionField.get(newValue);

      PrimaryIndex<Integer, Object> primaryById = (PrimaryIndex<Integer, Object>)dataStore.getPrimaryIndex(Integer.class, klass);
      Object value = primaryById.get(id);
      VersionTime newTime = (VersionTime)versionField.get(value);
      if (!oldTime.equals(newTime)) {
        throw new ConcurrentModificationException(oldTime + " vs " + newTime);
      }
      
      newTime = VersionTime.now();
      versionField.set(newValue, newTime);
      primaryById.put(newValue);
      transaction.commit();
      return newValue;
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException |
             IllegalAccessException | DatabaseException ex) {
      transaction.abort();
      throw new RuntimeException(ex);
    }
  }
  
}
