package org.gyfor.dbloader.berkeley;

import java.io.FileReader;
import java.io.IOException;

import org.gyfor.berkeleydb.DataEnvironment;
import org.gyfor.berkeleydb.DataTable;
import org.gyfor.berkeleydb.KeyDatabaseEntry;
import org.gyfor.berkeleydb.ObjectDatabaseEntry;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.util.RunTimer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Party;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.Transaction;

import au.com.bytecode.opencsv.CSVReader;

@Component (service = CVSDatabaseLoader1.class, immediate = true)
public class CVSDatabaseLoader1 {

  private DataEnvironment dataEnvironment;
  private IPlanContext planEnvironment;
  
  
  @Reference
  public void setDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = dataEnvironment;
  }
  
  public void unsetDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = null;
  }
  
  
  @Reference
  public void setPlanEnvironment (IPlanContext planEnvironment) {
    this.planEnvironment = planEnvironment;  
  }
  
  
  public void unsetPlanEnvironment (IPlanContext planEnvironment) {
    this.planEnvironment = null;  
  }
  
  
  @Activate
  public void activate () {
    RunTimer timer = new RunTimer();
    
    String className = "org.gyfor.datastore.Party";

    // Truncate the database (both primary and secondary)
    dataEnvironment.truncateDatabase(className);

    DataTable entityTable = dataEnvironment.openTable(planEnvironment, Party.class, false);

    ObjectDatabaseEntry data = entityTable.getDatabaseEntry();

    // Read csv data and write database records, including a secondary index
    try {
      CSVReader reader = new CSVReader(new FileReader("C:/Users/Kevin/git/gyfor/org.gyfor.dbloader.berkeley/party.csv"));
      String[] fieldNames = reader.readNext(); 

      String[] line = reader.readNext();
      while (line != null) {
         // line[] is an array of values from the CSV file
         System.out.println(line[0] + " " + line[1] + " ...");
         
         int id = Integer.parseInt(line[0]);
         KeyDatabaseEntry key = new KeyDatabaseEntry(id);

         Transaction txn2 = dataEnvironment.beginTransaction();
         try {
           data.setValue(line);
           entityTable.put(txn2, key, data);
           txn2.commit();
         } catch (DatabaseException ex) {
           ex.printStackTrace();
           txn2.abort();
         } catch (UserEntryException ex) {
           System.out.println(ex);
           txn2.abort();
         }
         line = reader.readNext();
      }
      reader.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    
    // Read the database: first in primary key order
    System.out.println("Primary key order:");
    try (Cursor cursor = entityTable.openCursor()) {
      OperationStatus status = cursor.getFirst(null, data, LockMode.DEFAULT);
      while (status == OperationStatus.SUCCESS) {
        Party party = data.getValue();
        System.out.println(party);
        status = cursor.getNext(null, data, LockMode.DEFAULT);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.out.println();
    
    // Read the database: in secondary key order
    System.out.println("ABN key order:");
    try (SecondaryCursor cursor = entityTable.openIndexCursor(1)) {
      OperationStatus status = cursor.getFirst(null, data, LockMode.DEFAULT);
      while (status == OperationStatus.SUCCESS) {
        Party party = data.getValue();
        System.out.println(party);
        status = cursor.getNext(null, data, LockMode.DEFAULT);
      }
    }
    System.out.println();
    
    entityTable.close();
    
    timer.report();
  }
  
}
