package org.gyfor.dao.berkeley;

import org.gyfor.dao.IDataAccessObject;
import org.gyfor.dao.IDataTableReference;
import org.gyfor.dao.IDataTableReferenceRegistry;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component (configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class DataTableReference implements IDataTableReference {

  private final Logger logger = LoggerFactory.getLogger(DataTableReference.class);
  
  
  private DataEnvironment dataEnvironment;
  
  private IPlanContext planContext;
  
  private IDataTableReferenceRegistry referenceRegistry;
  
  
  @Configurable(name="class", required=true)
  private String className;
  
  
  @Reference
  void setDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = dataEnvironment;
  }
  
  
  void unsetDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = null;
  }
  
  
  @Reference
  void setPlanContext (IPlanContext planContext) {
    this.planContext = planContext;
  }
  
  
  void unsetPlanContext (IPlanContext planContext) {
    this.planContext = null;
  }
  
  
  @Reference
  void setDataTableRegistry (IDataTableReferenceRegistry referenceRegistry) {
    this.referenceRegistry = referenceRegistry;
  }
  
  
  void unsetDataTableRegistry (IDataTableReferenceRegistry referenceRegistry) {
    this.referenceRegistry = null;
  }
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    logger.info ("Activating {} with {}", this.getClass(), className);
    System.out.println("activating " + this.getClass() + " with " + className);
    
    referenceRegistry.register(className, this);
  }

  
  @Deactivate
  public void deactivate () {
    logger.info ("Deactivating {} with {}", this.getClass(), className);
    
    referenceRegistry.unregister(className);
  }
  
  
  @Override
  public <T> IDataAccessObject<T> newDataAccessService(boolean readOnly) {
    return new DataAccessObject<T>(dataEnvironment, planContext, className, readOnly);
  }

}
