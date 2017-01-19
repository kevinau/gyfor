package org.gyfor.dao.berkeley;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.dao.IDataFetchRegistry;
import org.gyfor.dao.IDataFetchService;
import org.gyfor.dao.IDataUpdateService;
import org.gyfor.dao.IdValuePair;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.todo.NotYetImplementedException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;


@Component (configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class DataUpdateService extends DataFetchService implements IDataUpdateService {

  private final Logger logger = LoggerFactory.getLogger(DataUpdateService.class);
  
  
  private IPlanContext planContext;
  
  private DataEnvironment dataEnvironment;
  
  private IDataUpdateRegistry dataUpdateRegistry;
  
  
  @Configurable(name="class", required=true)
  private String className;
  
  
  @Configurable
  private boolean readOnly = true;

  private IEntityPlan<?> entityPlan;
  
  private DataTable dataTable;
  
  
  @Reference
  void setPlanContext (IPlanContext planContext) {
    this.planContext = planContext;
  }
  

  void unsetPlanContext (IPlanContext planContext) {
    this.planContext = null;
  }
  
  
  @Reference 
  void setDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = dataEnvironment;
  }
  

  void unsetDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = null;
  }
  
  
  @Reference
  void setDataAccessRegistry (IDataUpdateRegistry dataUpdateRegistry) {
    this.dataUpdateRegistry = dataUpdateRegistry;
  }
  
  
  void unsetDataAccessRegistry (IDataFetchRegistry dataAccessRegistry) {
    this.dataAccessRegistry = null;
  }
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    logger.info ("Activating {} with {}", this.getClass(), className);
    System.out.println("activating " + this.getClass() + " with " + className);
    
    entityPlan = planContext.getEntityPlan(className);
    dataTable = null;
    dataAccessRegistry.register(className, this);
  }

  
  @Deactivate
  public void deactivate () {
    logger.info ("Deactivating {} with {}", this.getClass(), className);
    
    dataAccessRegistry.unregister(className);
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

}
