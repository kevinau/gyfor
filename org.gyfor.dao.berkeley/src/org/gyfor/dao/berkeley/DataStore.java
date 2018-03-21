package org.gyfor.dao.berkeley;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;

import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.AnnotationModel;
import com.sleepycat.persist.model.EntityModel;


@Component(service=DataStore.class)
public class DataStore {

  @Reference
  private DataEnvironment envionment;
  
  @Configurable
  private String storeName = "EntityStore";
  
  private EntityStore store;
  

  @Activate 
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    EntityModel model = new AnnotationModel();
    model.registerClass(LocalDateProxy.class);
    model.registerClass(DecimalProxy.class);
    model.registerClass(CRC64DigestProxy.class);
    model.registerClass(VersionTimeProxy.class);

    StoreConfig storeConfig = new StoreConfig();
    storeConfig.setAllowCreate(true);
    storeConfig.setModel(model);
    storeConfig.setTransactional(true);
    
    // Open the entity store
    store = envionment.newEntityStore(storeName, storeConfig);
  }
  
  
  // TODO remove
  public EntityStore getEntityStore() {
    return store;
  }
  
  
  @Deactivate
  public void deactivate () {
    store.close();
  }
  
  
  public <PK,E> PrimaryIndex<PK,E> getPrimaryIndex (Class<PK> pkClass, Class<E> entityClass) {
    return store.getPrimaryIndex(pkClass, entityClass);
  }
  

  public <E> PrimaryIndex<Integer,E> getPrimaryIndex (Class<E> entityClass) {
    return getPrimaryIndex(Integer.class, entityClass);
  }
  

  public <SK,PK,E> SecondaryIndex<SK,PK,E> getSecondaryIndex (PrimaryIndex<PK,E> primaryIndex, Class<SK> skClass, String name) {
    return store.getSecondaryIndex(primaryIndex, skClass, name);
  }
  
  
  public <E> SecondaryIndex<String,Integer,E> getSecondaryIndex (PrimaryIndex<Integer,E> primaryIndex, String name) {
    return getSecondaryIndex(primaryIndex, String.class, name);
  }

  
  public Transaction beginTransaction() {
    return store.getEnvironment().beginTransaction(null, null);
  }
  
}
