package org.gyfor.doc;

import java.util.Arrays;
import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;


@Component(service=DocumentTypeRegistry.class)
public class DocumentTypeRegistry {

  private BundleContext context;
  
  @Activate
  public void activate (BundleContext context) {
    this.context = context;
  }
  

  @Deactivate
  public void deactivate (BundleContext context) {
    this.context = null;
  }
  
  
  public String[] getDocumentTypeIds () {
    try {
      Collection<ServiceReference<IDocumentType>> serviceRefs = context.getServiceReferences(IDocumentType.class, null);
      String[] names = new String[serviceRefs.size()];
      
      int i = 0;
      for (ServiceReference<IDocumentType> serviceRef : serviceRefs) {
        String name = (String)serviceRef.getProperty("documentTypeId");
        if (name == null) {
          throw new RuntimeException("IDocumentType component has no documentTypeId: " + serviceRef);
        }
        names[i++] = name;
      }
      Arrays.sort(names);
      return names;
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public IDocumentType getDialect (String name) {
    try {
      Collection<ServiceReference<IDocumentType>> serviceRefs = context.getServiceReferences(IDocumentType.class, "(documentTypeId=" + name + ")");
      if (serviceRefs.size() != 1) {
        throw new IllegalArgumentException(name);
      }
      ServiceReference<IDocumentType> serviceRef = serviceRefs.iterator().next();
      return (IDocumentType)context.getService(serviceRef);
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

}
