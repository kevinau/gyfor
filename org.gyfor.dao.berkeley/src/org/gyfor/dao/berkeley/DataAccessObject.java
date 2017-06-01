package org.gyfor.dao.berkeley;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.gyfor.dao.IDataAccessObject;
import org.gyfor.dao.IdValuePair;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.object.value.EntityLife;
import org.gyfor.todo.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.Transaction;


public class DataAccessObject<T> implements IDataAccessObject<T> {

  private final Logger logger = LoggerFactory.getLogger(DataAccessObject.class);
  
  
  private final DataEnvironment dataEnvironment;
  
  private final String className;
  
  private final IEntityPlan<T> entityPlan;
  
  private final boolean readOnly;
  
  private DataTable dataTable;
  private KeyDatabaseEntry keyEntry;
  private ObjectDatabaseEntry dataEntry;
  

  public DataAccessObject (DataEnvironment dataEnvironment, IPlanFactory planFactory, String className, boolean readOnly) {
    logger.info ("Creating data access service {} with {}", this.getClass(), className);
    
    this.dataEnvironment = dataEnvironment;
    this.className = className;
    this.readOnly = readOnly;
        
    entityPlan = planFactory.getEntityPlan(className);
    dataTable = null;
  }

  
  @Override
  public void close () {
    logger.info ("Closing {} with {}", this.getClass(), className);
    
    if (dataTable != null) {
      dataTable.close();
      dataTable = null;
    }
  }
  
  
  protected synchronized void open () {
    if (dataTable == null) {
      dataTable = dataEnvironment.openTable(entityPlan, readOnly);
      keyEntry = new KeyDatabaseEntry();
      dataEntry = dataTable.getDatabaseEntry();
    }
  }

  
  @Override
  public T getById(int id) {
    if (dataTable == null) {
      open();
    }
    KeyDatabaseEntry key = new KeyDatabaseEntry(id);
    ObjectDatabaseEntry data = dataTable.getDatabaseEntry();
    
    OperationStatus status = dataTable.get(null, key, data, LockMode.DEFAULT);
    switch (status) {
    case SUCCESS :
      return data.getValue();
    case NOTFOUND :
      return null;
    default :
      throw new RuntimeException("Unexpected status: " + status);
    }
  }


  @Override
  public T getByKey(int keyNo, Object... keyValues) {
    List<IItemPlan<?>[]> uniqueConstraints = entityPlan.getUniqueConstraints();
    if (keyNo > uniqueConstraints.size()) {
      throw new IllegalArgumentException("Key number number must be less than " + uniqueConstraints.size());
    }
    IItemPlan<?>[] itemPlans = uniqueConstraints.get(keyNo - 1);
    if (keyValues.length != itemPlans.length) {
      throw new IllegalArgumentException(keyValues.length + " key values when expecting " + itemPlans.length);
    }
    TableIndex index = dataTable.getIndex(keyNo);
    SecondaryCursor cursor = index.openCursor();
    cursor.get(key, data, getType, options)
    throw new NotYetImplementedException();
  }


  @Override
  public boolean existsByKey(Object... keyValues) {
    throw new NotYetImplementedException();
  }


  @Override
  public List<T> getAll() {
    if (dataTable == null) {
      open();
    }
    
    ObjectDatabaseEntry data = dataTable.getDatabaseEntry();
    List<T> results = new ArrayList<>();
    
    try (Cursor cursor = dataTable.openCursor()) {
      OperationStatus status = cursor.getFirst(null, data, LockMode.DEFAULT);
      while (status == OperationStatus.SUCCESS) {
        results.add(data.getValue());
        status = cursor.getNext(null, data, LockMode.DEFAULT);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return results;
  }


  @Override
  public List<IdValuePair<String>> getDescriptionAll() {
    if (dataTable == null) {
      open();
    }
    
    KeyDatabaseEntry key = new KeyDatabaseEntry();
    ObjectDatabaseEntry data = dataTable.getDatabaseEntry();
    List<IdValuePair<String>> results = new ArrayList<>();
    
    try (Cursor cursor = dataTable.openCursor()) {
      OperationStatus status = cursor.getFirst(key, data, LockMode.DEFAULT);
      while (status == OperationStatus.SUCCESS) {
        Object instance = data.getValue();
        String description = entityPlan.getDescription(instance);
        IdValuePair<String> idValue = new IdValuePair<String>(key.getInt(), description);
        results.add(idValue);
        status = cursor.getNext(null, data, LockMode.DEFAULT);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return results;
  }


  @Override
  public void add (T instance) {
    if (entityPlan.hasId() == false) {
      throw new NotYetImplementedException("Entity without an integer id");
    }

    if (dataTable == null) {
      open();
    }
    
    Transaction txn = dataEnvironment.beginTransaction();
    int id = entityPlan.getId(instance);
    if (id == 0) {
      id = dataTable.getNextSequence(txn);
      entityPlan.setId(instance, id);
    }
    keyEntry.setInt(id);
    
    if (entityPlan.hasVersion()) {
      Timestamp now = new Timestamp(System.currentTimeMillis());
      entityPlan.setVersion(instance, now);
    }
    
    if (entityPlan.hasEntityLife()) {
      entityPlan.setEntityLife(instance, EntityLife.ACTIVE);
    }
    dataEntry.setValue(instance);
    dataTable.put(txn, keyEntry, dataEntry);
    txn.commit();
  }


  @Override
  public void update (T entity) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void addOrUpdate (T instance) {
    if (entityPlan.hasId() == false) {
      throw new NotYetImplementedException("Entity without an integer id");
    }

    if (dataTable == null) {
      open();
    }
    
    int id = entityPlan.getId(instance);
    if (id == 0) {
      // Entity has not been stored, so add it
      add (instance);
    }
    // TODO Auto-generated method stub
    
  }


  @Override
  public void remove (T entity) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public IEntityPlan<T> getEntityPlan() {
    return entityPlan;
  }

}
