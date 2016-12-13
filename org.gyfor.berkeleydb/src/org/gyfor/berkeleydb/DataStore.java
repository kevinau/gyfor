package org.gyfor.berkeleydb;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.AnnotationModel;
import com.sleepycat.persist.model.EntityModel;


@Component(service=DataStore.class, immediate = true)
public class DataStore {

  private DataEnvironment envionment;
  
  @Configurable
  private String storeName = "EntityStore";
  
  private EntityStore store;
  
  
  @Reference
  public void setDataEnvironment (DataEnvironment environment) {
    this.envionment = environment;
  }
  
  
  public void unsetDataEnvironment (DataEnvironment environment) {
    this.envionment = null;
  }
  
  
  @Activate 
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    EntityModel model = new AnnotationModel();
    model.registerClass(LocalDateProxy.class);
    model.registerClass(DecimalProxy.class);
    model.registerClass(CRC64DigestProxy.class);

    StoreConfig storeConfig = new StoreConfig();
    storeConfig.setAllowCreate(true);
    storeConfig.setModel(model);
    storeConfig.setTransactional(true);
    
    // Open the entity store
    store = envionment.newEntityStore(storeName, storeConfig);
  }
  
  
  @Deactivate
  public void deactivate () {
    store.close();
  }
  
  
  public <PK,E> PrimaryIndex<PK,E> getPrimaryIndex (Class<PK> pkClass, Class<E> entityClass) {
    return store.getPrimaryIndex(pkClass, entityClass);
  }
  

  public <SK,PK,E> SecondaryIndex<SK,PK,E> getSecondaryIndex (PrimaryIndex<PK,E> primaryIndex, Class<SK> skClass, String name) {
    return store.getSecondaryIndex(primaryIndex, skClass, name);
  }

}
