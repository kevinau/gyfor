package org.gyfor.dbloader.berkeley;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.gyfor.berkeleydb.KeyDatabaseEntry;
import org.gyfor.berkeleydb.ObjectDatabaseEntry;
import org.gyfor.dao.IDataAccessService;
import org.gyfor.dao.IDataTableReferenceRegistry;
import org.gyfor.object.UserEntryException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Party;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;

import au.com.bytecode.opencsv.CSVReader;

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
      // Read csv data and write database records, including a secondary index
      try (IDataAccessService<?> das = e.newDataAccessService(true)) {
        KeyDatabaseEntry key = new KeyDatabaseEntry();
        ObjectDatabaseEntry data = das.getDatabaseEntry();

        CSVReader reader = new CSVReader(new FileReader("C:/Users/Kevin/git/gyfor/org.gyfor.dbloader.berkeley/party.csv"));
        String[] fieldNames = reader.readNext(); 

        String[] line = reader.readNext();
        while (line != null) {
           // line[] is an array of values from the CSV file
           System.out.println("Adding: " + line[0] + " " + line[1] + " " + line[2] + " ...");
           
           int id = Integer.parseInt(line[0]);
           data.setValue(line);
           das.addOrUpdate(data);
           
           line = reader.readNext();
        }
        reader.close();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      
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
