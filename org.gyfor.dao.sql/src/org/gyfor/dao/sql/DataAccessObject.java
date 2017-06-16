package org.gyfor.dao.sql;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.dao.DescriptionChangeListener;
import org.gyfor.dao.EntityChangeListener;
//import org.gyfor.dao.IDataEventRegistry;
import org.gyfor.dao.EntityDescription;
import org.gyfor.dao.IDataAccessObject;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IConnectionFactory;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;


//////@Component (configurationPolicy=ConfigurationPolicy.REQUIRE)
public class DataAccessObject<T> implements IDataAccessObject<T> {

  //private IDataEventRegistry dataEventRegistry;
  
  @Reference(name="connFactory")
  private IConnectionFactory connFactory;
  
  @Reference
  private IPlanFactory planFactory;
    
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
  
  
//  @Reference(name="connFactory")
//  void setConnFactory (IConnectionFactory connFactory) {
//    this.connFactory = connFactory;
//  }
//  
//  
//  void unsetConnFactory (IConnectionFactory connFactory) {
//    this.connFactory = null;
//  }
//  
//  
//  
//  @Reference
//  void setPlanFactory (IPlanFactory planFactory) {
//    this.planFactory = planFactory;
//  }
//  
//  
//  void unsetPlanFactory (IPlanFactory planFactory) {
//    this.planFactory = null;
//  }
  
  private IEntityPlan<T> entityPlan;
  
  private SQLBuilder<T> sqlBuilder;
  
  private IItemPlan<Integer> idPlan;
  private IItemPlan<VersionTime> versionPlan;
  private List<IItemPlan<?>> dataPlans;
  private IItemPlan<EntityLife> entityLifePlan;

  private List<EntityChangeListener<T>> entityChangeListenerList = new ArrayList<>(10);
  private List<DescriptionChangeListener> descriptionChangeListenerList = new ArrayList<>(10);
  
  
  @Activate 
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    entityPlan = planFactory.getEntityPlan(className);
    idPlan = entityPlan.getIdPlan();
    versionPlan = entityPlan.getVersionPlan();
    dataPlans = entityPlan.getDataPlans();
    entityLifePlan = entityPlan.getEntityLifePlan();
    
    sqlBuilder = new SQLBuilder<T>(entityPlan, schema);
  }
  
  
  @Deactivate 
  public void deactivate () {
  }
  
  
//  private void sendEvent (String eventName, Dictionary<String, ?> props) {
//    @SuppressWarnings("unchecked")
//    ServiceReference<EventAdmin> ref = (ServiceReference<EventAdmin>)bundleContext.getServiceReference(EventAdmin.class.getName());
//    System.out.println("***************** send " + eventName + " event");
//    if (ref != null) {
//      EventAdmin eventAdmin = bundleContext.getService(ref);
//      Event event = new Event(EVENT_BASE + eventName, props);
//      eventAdmin.sendEvent(event);
//    }
//  }
  

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
  public void addEntityChangeListener (EntityChangeListener<T> x) {
    entityChangeListenerList.add(x);
  }
  
  
  @Override
  public void removeEntityChangeListener (EntityChangeListener<T> x) {
    entityChangeListenerList.remove(x);
  }
  
  
  @Override
  public void addDescriptionChangeListener (DescriptionChangeListener x) {
    descriptionChangeListenerList.add(x);
  }
  
  
  @Override
  public void removeDescriptionChangeListener (DescriptionChangeListener x) {
    descriptionChangeListenerList.remove(x);
  }
  
  
  private void fireEntityAddedEvent (T entity) {
    for (EntityChangeListener<T> x : entityChangeListenerList) {
      x.entityAdded(entity);
    }
  }
  
  
  private void fireEntityRetiredEvent (T entity) {
    for (EntityChangeListener<T> x : entityChangeListenerList) {
      x.entityRetired(entity);
    }
  }
  
  
  private void fireEntityUnretiredEvent (T entity) {
    for (EntityChangeListener<T> x : entityChangeListenerList) {
      x.entityUnretired(entity);
    }
  }
  
  
  private void fireEntityChangedEvent (T fromEntity, T toEntity) {
    for (EntityChangeListener<T> x : entityChangeListenerList) {
      x.entityChanged(fromEntity, toEntity);
    }
  }
  
  
  private void fireEntityRemovedEvent (T entity) {
    for (EntityChangeListener<T> x : entityChangeListenerList) {
      x.entityRemoved(entity);
    }
  }
  
  
  private void fireDescriptionAddedEvent (int id, T instance, EntityLife entityLife) {
    EntityDescription description = null;
    for (DescriptionChangeListener x : descriptionChangeListenerList) {
      if (description == null) {
        String text = entityPlan.getDescription(instance);
        description = new EntityDescription(id, text, entityLife);
      }
      x.descriptionAdded(description);
    }
  }
  
  
  private void fireDescriptionChangedEvent (int id, String text, T instance) {
    EntityDescription description = null;
    for (DescriptionChangeListener x : descriptionChangeListenerList) {
      if (description == null) {
        EntityLife entityLife = entityPlan.getEntityLife(instance);
        description = new EntityDescription(id, text, entityLife);
      }
      x.descriptionChanged(description);
    }
  }
  
  
  private void fireDescriptionChangedEvent (int id, T instance, EntityLife entityLife) {
    EntityDescription description = null;
    for (DescriptionChangeListener x : descriptionChangeListenerList) {
      if (description == null) {
        String text = entityPlan.getDescription(instance);
        description = new EntityDescription(id, text, entityLife);
      }
      x.descriptionChanged(description);
    }
  }
  
  
  private void fireDescriptionRemovedEvent (int id) {
    for (DescriptionChangeListener x : descriptionChangeListenerList) {
      x.descriptionRemoved(id);
    }
  }
  
  
  @Override
  public void change (T oldInstance, T newInstance) {
    int id = idPlan.getFieldValue(newInstance);
    VersionTime version = VersionTime.now();

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
        versionPlan.setFieldValue(newInstance, version);
      }
      fireEntityChangedEvent(oldInstance, newInstance);
      
      String oldDesc = entityPlan.getDescription(oldInstance);
      String newDesc = entityPlan.getDescription(newInstance);
      if (!oldDesc.equals(newDesc)) {
        // Notify any listeners that the entity description has changed
        fireDescriptionChangedEvent(id, newDesc, newInstance);
      }
    }
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
    int id = idPlan.getFieldValue(oldInstance);
    
    try (
      IConnection conn = connFactory.getIConnection();
      IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getDeleteSql())) 
    {
      // TODO verify that the version has not changed (as part of the delete)
      idPlan.setStatementFromInstance(stmt, oldInstance);
      stmt.executeUpdate();
      
      // Notify any listeners that the entity has been removed
      fireEntityRemovedEvent(oldInstance);
      fireDescriptionRemovedEvent(id);
    }
  }
  
  
  @Override
  public void add (T instance) {
    int oldId = idPlan.getFieldValue(instance);
    if (oldId != -1) {
      throw new IllegalArgumentException("instance value appears to be complete (ie it already has a data store id value)");
    }

    VersionTime version = null;
    int id;
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt1 = conn.prepareStatement(sqlBuilder.getNextValSql());
        IPreparedStatement stmt2 = conn.prepareStatement(sqlBuilder.getInsertSql())) 
    {
      conn.setAutoCommit(false);
      
      IResultSet rs = stmt1.executeQuery();
      rs.next();
      id = rs.getInt();
      version = VersionTime.now();
      
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

      // Complete instance with id, version and entity life
      idPlan.setFieldValue(instance, id);
      if (versionPlan != null) {
        versionPlan.setFieldValue(instance, version);
      }
      if (entityLifePlan != null) {
        entityLifePlan.setFieldValue(instance, EntityLife.ACTIVE);
      }
      conn.commit();
      
      // Notify any listeners that a new entity has been added to the database
      fireEntityAddedEvent (instance);
      fireDescriptionAddedEvent (id, instance, EntityLife.ACTIVE);
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void add (IEntityModel model, T instance) {
    int oldId = idPlan.getFieldValue(instance);
    if (oldId != -1) {
      throw new IllegalArgumentException("instance value appears to be complete (ie it already has a data store id value)");
    }

    model.walkModel((m)-> {}, (m)-> {});
    
    VersionTime version = null;
    int id;
    
    try (
        IConnection conn = connFactory.getIConnection();
    {
      conn.setAutoCommit(false);
      
      IResultSet rs = stmt1.executeQuery();
      rs.next();
      id = rs.getInt();
      version = VersionTime.now();
      
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

      // Complete instance with id, version and entity life
      idPlan.setFieldValue(instance, id);
      if (versionPlan != null) {
        versionPlan.setFieldValue(instance, version);
      }
      if (entityLifePlan != null) {
        entityLifePlan.setFieldValue(instance, EntityLife.ACTIVE);
      }
      conn.commit();
      
      // Notify any listeners that a new entity has been added to the database
      fireEntityAddedEvent (instance);
      fireDescriptionAddedEvent (id, instance, EntityLife.ACTIVE);
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  private void updateEntityLife (T instance, EntityLife entityLife) {
    VersionTime version = null;
    
    try (
      IConnection conn = connFactory.getIConnection();
      IPreparedStatement stmt = conn.prepareStatement(sqlBuilder.getLifeUpdateSql()))
    {
      // TODO verify that the version has not changed (as part of the update)
      version = VersionTime.now();
      if (versionPlan != null) {
        versionPlan.setStatementFromValue(stmt, version);
      }
      entityLifePlan.setStatementFromValue(stmt, entityLife);
      idPlan.setStatementFromInstance(stmt, instance);
      stmt.executeUpdate();
      
      // Notify any listeners that the entity life has changed
//      Dictionary<String, Object> props = new Hashtable<>();
//      props.put("entity", entityPlan.getEntityName());
//      props.put("id", id);
//      props.put("entityLife", entityLife);
//      sendEvent("SEARCHCHANGE", props);
    }
//    return version;
  }
  
  
  @Override
  public void retire (T instance) {
    updateEntityLife (instance, EntityLife.RETIRED);
    fireEntityRetiredEvent(instance);
    
    int id = entityPlan.getId(instance);
    fireDescriptionChangedEvent(id, instance, EntityLife.RETIRED);
  }
  
  
  @Override
  public void unretire(T instance) {
    updateEntityLife (instance, EntityLife.ACTIVE);
    fireEntityUnretiredEvent (instance);
    
    int id = entityPlan.getId(instance);
    fireDescriptionChangedEvent(id, instance, EntityLife.ACTIVE);
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
