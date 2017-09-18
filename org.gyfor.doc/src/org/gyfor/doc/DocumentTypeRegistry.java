package org.gyfor.doc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.gyfor.object.value.EntityDescription;
import org.gyfor.object.value.EntityLife;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
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
  
  
  public IDocumentType getDocumentType (String documentName) {
    try {
      Collection<ServiceReference<IDocumentType>> serviceRefs = context.getServiceReferences(IDocumentType.class, "(name=" + documentName + ")");
      if (serviceRefs.size() != 1) {
        throw new IllegalArgumentException(documentName);
      }
      ServiceReference<IDocumentType> serviceRef = serviceRefs.iterator().next();
      return (IDocumentType)context.getService(serviceRef);
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public String[] getDocumentTypeNames () {
    try {
      Collection<ServiceReference<IDocumentType>> serviceRefs = context.getServiceReferences(IDocumentType.class, null);
      String[] names = new String[serviceRefs.size()];
      
      int i = 0;
      for (ServiceReference<IDocumentType> serviceRef : serviceRefs) {
        String name = (String)serviceRef.getProperty("name");
        if (name == null) {
          throw new RuntimeException("IDocumentType component has no name: " + serviceRef);
        }
        names[i++] = name;
      }
      Arrays.sort(names);
      return names;
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public List<EntityDescription> getAllDescriptions() {
    List<EntityDescription> descriptions = new ArrayList<>();
    try {
      Collection<ServiceReference<IDocumentType>> serviceRefs = context.getServiceReferences(IDocumentType.class, null);
      for (ServiceReference<IDocumentType> serviceRef : serviceRefs) {
        System.out.println("........... serviceRef " + serviceRef);
        IDocumentType docType = context.getService(serviceRef);
        EntityDescription description = new EntityDescription(docType.getName(), docType.getDescription(), EntityLife.ACTIVE);
        descriptions.add(description);
      }
      Collections.sort(descriptions);
      return descriptions;
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }
 
}
