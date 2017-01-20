package org.gyfor.dao.berkeley;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.dao.DataChangeListener;
import org.gyfor.dao.IDataAccessService;
import org.gyfor.dao.IdValuePair;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.todo.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


public class DataAccessService implements IDataAccessService {

  private final Logger logger = LoggerFactory.getLogger(DataFetchService.class);
  
  
  private DataEnvironment dataEnvironment;
  
  private String className;
  
  private IEntityPlan<?> entityPlan;
  
  private DataTable dataTable;
  
  

  public DataAccessService (DataEnvironment dataEnvironment, IPlanContext planContext, String className) {
    logger.info ("Creating data access service {} with {}", this.getClass(), className);
    
    this.dataEnvironment = dataEnvironment;
        
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
  
  
  protected void open () {
    open (true);
  }
  
  
  protected synchronized void open (boolean readOnly) {
    if (dataTable == null) {
      dataTable = dataEnvironment.openTable(entityPlan, readOnly);
    }
  }

  
  @Override
  public <T> T getById(int id) {
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
  public <T> T getByKey(Object... keyValues) {
    throw new NotYetImplementedException();
  }


  @Override
  public boolean existsByKey(Object... keyValues) {
    throw new NotYetImplementedException();
  }


  @Override
  public <T> List<T> getAll() {
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
  public void add(Object entity) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void update(int id, Object entity) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void put(Object entity) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void remove(int id) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void addDataChangeListener(DataChangeListener x) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void removeDataChangeListener(DataChangeListener x) {
    // TODO Auto-generated method stub
    
  }

}
