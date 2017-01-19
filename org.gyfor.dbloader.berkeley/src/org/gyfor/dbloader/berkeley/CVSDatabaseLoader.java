package org.gyfor.dbloader.berkeley;

import java.util.List;

import org.gyfor.dao.IDataFetchRegistry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Party;

@Component (service = CVSDatabaseLoader.class, immediate = true)
public class CVSDatabaseLoader {

  private IDataFetchRegistry dataFetchRegistry;
  
  
  @Reference
  public void setDataFetchRegistry (IDataFetchRegistry dataFetchRegistry) {
    this.dataFetchRegistry = dataFetchRegistry;
  }
  
  
  public void unsetDataFetchRegistry (IDataFetchRegistry dataFetchRegistry) {
    this.dataFetchRegistry = null;
  }
  
    
  @Activate
  public void activate () {
    System.out.println("activate CVS database loader.................." + dataFetchRegistry);
    String className = "org.pennyledger.party.Party";
    
    dataFetchRegistry.getService(className, e -> {
      // Read the database: first in primary key order
      System.out.println("Primary key order:");
      List<Party> results = e.getAll();
      for (Party party : results) {
        System.out.println(party);
      }
    });
  }

}
