package org.gyfor.dao;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;


@Component(service=EntitySetRegistry.class, configurationPolicy=ConfigurationPolicy.IGNORE)
public class EntitySetRegistry {

  private BundleContext context;
  
  @Activate
  public void activate (BundleContext context) {
    this.context = context;
  }
  

  @Deactivate
  public void deactivate (BundleContext context) {
    this.context = null;
  }
  
  
  public IEntitySet getEntitySet (String entityName) {
    try {
      Collection<ServiceReference<IEntitySet>> serviceRefs = context.getServiceReferences(IEntitySet.class, "(name=" + entityName + ")");
      if (serviceRefs.size() == 0) {
        return null;
      }
      ServiceReference<IEntitySet> serviceRef = serviceRefs.iterator().next();
      return (IEntitySet)context.getService(serviceRef);
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

}
