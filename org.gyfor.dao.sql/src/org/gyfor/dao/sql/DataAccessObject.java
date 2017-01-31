package org.gyfor.dao.sql;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.gyfor.dao.DataAddStatus;
import org.gyfor.dao.IDataAccessObject;
//import org.gyfor.dao.IDataEventRegistry;
import org.gyfor.dao.EntityDescription;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionValue;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IConnectionFactory;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;


@Component (configurationPolicy=ConfigurationPolicy.REQUIRE)
public class DataAccessObject<T> implements IDataAccessObject<T> {

  //private IDataEventRegistry dataEventRegistry;
  
  private IConnectionFactory connFactory;
  
  private IPlanContext planContext;
  
  private BundleContext bundleContext;
  
  
  @Configurable(name="class", required=true)
  private String className;
  
  @Configurable
  private String schema;
  
  
//  @Reference
//  void setDataEventRegistry (IDataEventRegistry dataEventRegistry) {
//    this.dataEventRegistry = dataEventRegistry;
//  }
//  
//  
//  void unsetDataEventRegistry (IDataEventRegistry dataEventRegistry) {
//    this.dataEventRegistry = null;
//  }
  
  
  @Reference(name="connFactory")
  void setConnFactory (IConnectionFactory connFactory) {
    this.connFactory = connFactory;
  }
  
  
  void unsetConnFactory (IConnectionFactory connFactory) {
    this.connFactory = null;
  }
  
  
  @Reference
  void setPlanContext (IPlanContext planContext) {
    this.planContext = planContext;
  }
  
  
  void unsetPlanContext (IPlanContext planContext) {
    this.planContext = null;
  }
  
  private IEntityPlan<T> entityPlan;
  
  private SQLBuilder<T> sqlBuilder;
  
  private IItemPlan<Integer> idPlan;
  private IItemPlan<VersionValue> versionPlan;
  private List<IItemPlan<?>> dataPlans;
  private IItemPlan<EntityLife> entityLifePlan;

  
  @Activate 
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    bundleContext = componentContext.getBundleContext();

    entityPlan = planContext.getEntityPlan(className);
    idPlan = entityPlan.getIdPlan();
    versionPlan = entityPlan.getVersionPlan();
    dataPlans = entityPlan.getDataPlans();
    entityLifePlan = entityPlan.getEntityLifePlan();
    
    sqlBuilder = new SQLBuilder<T>(entityPlan, schema);
  }
  
  
  @Deactivate 
  public void deactivate () {
  }
  
  
  private void sendEvent (String eventName, Dictionary<String, ?> props) {
    @SuppressWarnings("unchecked")
    ServiceReference<EventAdmin> ref = (ServiceReference<EventAdmin>)bundleContext.getServiceReference(EventAdmin.class.getName());
    if (ref != null) {
      EventAdmin eventAdmin = bundleContext.getService(ref);
      Event event = new Event("org/gyfor/data/DataAccessObject/" + eventName, props);
      eventAdmin.sendEvent(event);
    }
  }
  

  private void getAllColumns (T instance, IResultSet rs) {
    idPlan.setInstanceFromResult(instance, rs);
    if (versionPlan != null) {
      versionPlan.setInstanceFromResult(instance, rs);
    }
    for (IItemPlan<?> dataPlan : dataPlans) {
      dataPlan.setInstanceFromResult(instance, rs);
    }
    if (entityLifePlan != null) {
      entityLifePlan.setInstanceFromResult(instance, rs);
    }
  }
  
  
  @Override
  public VersionValue update (T oldInstance, T newInstance) {
    int id = idPlan.getValue(newInstance);
    VersionValue version = VersionValue.now();

    try (
      IConnection conn = connFactory.getIConnection();
      IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getDataUpdateSql()))
    {
      // TODO verify that the version has not changed (as part of the update)
      if (versionPlan != null) {
        versionPlan.setStatementFromValue(stmt, version);
      }
      for (IItemPlan<?> plan : dataPlans) {
        plan.setStatementFromInstance(stmt, newInstance);
      }
      idPlan.setStatementFromValue(stmt, id);
      stmt.executeUpdate();

      if (versionPlan != null) {
        versionPlan.setValue(newInstance, version);
      }
      
      String oldDesc = entityPlan.getDescription(oldInstance);
      String newDesc = entityPlan.getDescription(newInstance);
      if (!oldDesc.equals(newDesc)) {
        // Notify any listeners that the entity description has changed
        Dictionary<String, Object> props = new Hashtable<>();
        props.put("entity", entityPlan.getEntityName());
        props.put("id", id);
        props.put("description", newDesc);
        sendEvent("SEARCHCHANGE", props);
      }
    }
    return version;
  }
  
  
  @Override
  public void removeAll () {
    try (
      IConnection conn = connFactory.getIConnection();
      IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getDeleteAllSql()))
    {
      stmt.executeUpdate();
    }
  }


  @Override
  public void remove (T oldInstance) {
    int id = idPlan.getValue(oldInstance);
    
    try (
      IConnection conn = connFactory.getIConnection();
      IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getDeleteSql())) 
    {
      // TODO verify that the version has not changed (as part of the delete)
      idPlan.setStatementFromInstance(stmt, oldInstance);
      stmt.executeUpdate();
      
      // Notify any listeners that the entity has been removed
      Dictionary<String, Object> props = new Hashtable<>();
      props.put("entity", entityPlan.getEntityName());
      props.put("id", id);
      sendEvent("REMOVED", props);
    }
  }
  
  
  @Override
  public DataAddStatus add (T instance) {
    VersionValue version = null;
    int id;
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt1 = conn.prepareStatement(sqlBuilder.getNextValSql());
        IPreparedStatement stmt2 = conn.prepareStatement(sqlBuilder.getInsertSql())) {
      
      // TODO wrap the two statement executions in a transaction
      IResultSet rs = stmt1.executeQuery();
      rs.next();
      id = rs.getInt();
      version = VersionValue.now();
      
      idPlan.setStatementFromValue(stmt2, id);
      if (versionPlan != null) {
        versionPlan.setStatementFromValue(stmt2, version);
      }
      for (IItemPlan<?> plan : dataPlans) {
        plan.setStatementFromInstance(stmt2, instance);
      }
      if (entityLifePlan != null) {
        entityLifePlan.setStatementFromValue(stmt2, EntityLife.ACTIVE);
      }
      stmt2.executeUpdate();

      idPlan.setValue(instance, id);
      if (versionPlan != null) {
        versionPlan.setValue(instance, version);
      }
      if (entityLifePlan != null) {
        entityLifePlan.setValue(instance, EntityLife.ACTIVE);
      }
      
      // Notify any listeners that a new entity has been added to the database
      Dictionary<String, Object> props = new Hashtable<>();
      props.put("entity", entityPlan.getEntityName());
      props.put("id", id);
      props.put("description", entityPlan.getDescription(instance));
      sendEvent("ADDED", props);
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    }
    return new DataAddStatus(id, version);
  }
  
  
  private VersionValue updateEntityLife (int id, EntityLife entityLife) {
    VersionValue version = null;
    
    try (
      IConnection conn = connFactory.getIConnection();
      IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getLifeUpdateSql()))
    {
      // TODO verify that the version has not changed (as part of the update)
      version = VersionValue.now();
      if (versionPlan != null) {
        versionPlan.setStatementFromValue(stmt, version);
      }
      entityLifePlan.setStatementFromValue(stmt, entityLife);
      idPlan.setStatementFromValue(stmt, id);
      stmt.executeUpdate();
      
      // Notify any listeners that the entity life has changed
      Dictionary<String, Object> props = new Hashtable<>();
      props.put("entity", entityPlan.getEntityName());
      props.put("id", id);
      props.put("entityLife", entityLife);
      sendEvent("SEARCHCHANGE", props);
    }
    return version;
  }
  
  
  @Override
  public VersionValue retire (int id) {
    return updateEntityLife (id, EntityLife.RETIRED);
  }
  
  
  @Override
  public VersionValue unretire(int id) {
    return updateEntityLife (id, EntityLife.ACTIVE);
  }


  @Override
  public T getById(int id) {
    T instance = entityPlan.newInstance();
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getFetchByIdSql()))
    {
      idPlan.setStatementFromValue(stmt, id);
      IResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        getAllColumns(instance, rs);
        return instance;
      } else {
        return null;
      }
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public boolean existsUnique(int uniqueIndex, Object[] values, int id) {
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getQueryUniqueSql(uniqueIndex)))
    {
      IItemPlan<?>[] uniquePlans = entityPlan.getUniqueConstraints().get(uniqueIndex);
      
      int i = 0;
      for (IItemPlan<?> plan : uniquePlans) {
        ((IItemPlan<Object>)plan).setStatementFromValue(stmt, values[i]);
        i++;
      }
      stmt.setInt(id);
      IResultSet rs = stmt.executeQuery();
      boolean found = rs.next();
      rs.close();
      return found;
    }
  }


  @Override
  public List<T> getAll() {
    List<T> results = new ArrayList<>();
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getFetchAllSql())) {
       
      IResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        T instance = entityPlan.newInstance();
        
        idPlan.setInstanceFromResult(instance, rs);
        if (versionPlan != null) {
          versionPlan.setInstanceFromResult(instance, rs);
        }
        for (IItemPlan<?> plan : dataPlans) {
          plan.setInstanceFromResult(instance, rs);
        }
        if (entityLifePlan != null) {
          entityLifePlan.setInstanceFromResult(instance, rs);
        }
        results.add(instance);
      }
    }
    return results;
  }
  
  
  @Override
  public String getDescriptionById (int id) {
    T instance = entityPlan.newInstance();
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getFetchByIdSql()))
    {
      idPlan.setStatementFromValue(stmt, id);
      IResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        getAllColumns(instance, rs);
        return entityPlan.getDescription(instance);
      } else {
        return null;
      }
    }
  }


  @Override
  public List<EntityDescription> getDescriptionAll() {
    List<EntityDescription> results = new ArrayList<>();
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getFetchDescriptionAllSql())) {
       
      IResultSet rs = stmt.executeQuery();
      T instance = entityPlan.newInstance();
      List<IItemPlan<?>> descPlans = entityPlan.getDescriptionPlans();
      while (rs.next()) {
        int id = rs.getInt();
        for (IItemPlan<?> plan : descPlans) {
          plan.setInstanceFromResult(instance, rs);
        }
        EntityLife entityLife;
        if (entityLifePlan != null) {
          entityLife = entityLifePlan.getResultValue(rs);
        } else {
          entityLife = EntityLife.ACTIVE;
        }
        
        String desc = entityPlan.getDescription(instance);
        
        EntityDescription idValue = new EntityDescription(id, desc, entityLife);
        results.add(idValue);
      }
    }
    return results;
  }


  @Override
  public void close() {
  }


  @Override
  public IEntityPlan<T> getEntityPlan() {
    return entityPlan;
  }


  @Override
  public T newInstance(T fromValue) {
    return entityPlan.newInstance(fromValue);
  }

}
