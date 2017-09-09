package org.gyfor.busnum;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;


@Component(service=BusinessLookupRegistry.class, configurationPolicy=ConfigurationPolicy.IGNORE)
public class BusinessLookupRegistry {

  private BundleContext context;
  
  @Activate
  public void activate (BundleContext context) {
    this.context = context;
  }
  

  @Deactivate
  public void deactivate (BundleContext context) {
    this.context = null;
  }
  
  
  public IBusinessLookup getBusinessLookup (String country) {
    try {
      Collection<ServiceReference<IBusinessLookup>> serviceRefs = context.getServiceReferences(IBusinessLookup.class, "(country=" + country + ")");
      if (serviceRefs.size() == 0) {
        return null;
      }
      ServiceReference<IBusinessLookup> serviceRef = serviceRefs.iterator().next();
      return (IBusinessLookup)context.getService(serviceRef);
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

}
