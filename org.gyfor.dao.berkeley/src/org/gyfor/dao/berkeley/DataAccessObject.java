package org.gyfor.dao.berkeley;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.dao.IDataAccessObject;
import org.gyfor.dao.IdValuePair;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.todo.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


public class DataAccessObject<T> implements IDataAccessObject<T> {

  private final Logger logger = LoggerFactory.getLogger(DataAccessObject.class);
  
  
  private DataEnvironment dataEnvironment;
  
  private String className;
  
  private IEntityPlan<T> entityPlan;
  
  private DataTable dataTable;
  
  private boolean readOnly;
  

  public DataAccessObject (DataEnvironment dataEnvironment, IPlanContext planContext, String className, boolean readOnly) {
    logger.info ("Creating data access service {} with {}", this.getClass(), className);
    
    this.dataEnvironment = dataEnvironment;
    this.readOnly = readOnly;
        
    entityPlan = planContext.getEntityPlan(className);
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
  public T getByKey(Object... keyValues) {
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
  public void add (T entity) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void update (T entity) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void addOrUpdate (T entity) {
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
