package org.gyfor.dbloader.berkeley;

import java.util.List;

import org.gyfor.dao.IDataAccessService;
import org.gyfor.dao.IDataTableReferenceRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Party;

@Component (service = CVSDatabaseLoader.class, immediate = true)
public class CVSDatabaseLoader {

  private IDataTableReferenceRegistry referenceRegistry;
  
  
  @Reference
  public void setDataTableRegistry (IDataTableReferenceRegistry referenceRegistry) {
    this.referenceRegistry = referenceRegistry;
  }
  
  
  public void unsetDataTableRegistry (IDataTableReferenceRegistry referenceRegistry) {
    this.referenceRegistry = null;
  }
  
    
  @Activate
  public void activate () {
    System.out.println("activate CVS database loader.................." + referenceRegistry);
    String className = "org.pennyledger.party.Party";
    
    referenceRegistry.getService(className, e -> {
      // Read the database: first in primary key order
      try (IDataAccessService das = e.newDataAccessService(true)) {
        System.out.println("Primary key order:");
        List<Party> results = das.getAll();
        for (Party party : results) {
          System.out.println(party);
        }
      }
    });
  }

}
