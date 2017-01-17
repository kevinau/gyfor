package org.gyfor.dbloader.berkeley;

import java.io.FileReader;
import java.io.IOException;

import org.gyfor.berkeleydb.DataEnvironment;
import org.gyfor.berkeleydb.DatabaseEntryFields;
import org.gyfor.berkeleydb.IDatabaseEntryFields;
import org.gyfor.berkeleydb.KeyDatabaseEntry;
import org.gyfor.berkeleydb.ObjectDatabaseEntry;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.plan.impl.PlanContext;
import org.gyfor.util.RunTimer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Party;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;
import com.sleepycat.je.Transaction;

import au.com.bytecode.opencsv.CSVReader;

//@Component (service = CVSDatabaseLoader2.class, immediate = true)
public class CVSDatabaseLoader2 {

  private DataEnvironment databaseEnvironment;
  
  
  @Reference
  public void setDataEnvironment (DataEnvironment databaseEnvironment) {
    this.databaseEnvironment = databaseEnvironment;
  }
  
  public void unsetDataEnvironment (DataEnvironment databaseEnvironment) {
    this.databaseEnvironment = null;
  }
  
  
  @Activate
  public void activate () {
    RunTimer timer = new RunTimer();
    
    String className = "org.gyfor.datastore.Party";
//    Class<?> klass;
//    try {
//      klass = Class.forName(className);
//    } catch (ClassNotFoundException ex) {
//      throw new RuntimeException(ex);
//    }
//    PlanFactory context = new PlanFactory();
//    IEntityPlan<?> entityPlan = EntityPlanFactory.getEntityPlan(context, klass);
//    
//    RootModel rootModel = new RootModel();
//    EntityModel entityModel = rootModel.buildEntityModel(entityPlan);

    // Truncate the database (both primary and secondary)
    databaseEnvironment.truncateDatabase(className);

    // Open the database. Create it if it does not already exist.
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setAllowCreate(true);
    dbConfig.setTransactional(true);
    Database entityDatabase = databaseEnvironment.openDatabase(null, className, dbConfig); 

    PlanContext planEnvmt = new PlanContext();
    ObjectDatabaseEntry data = new ObjectDatabaseEntry(planEnvmt, Party.class);

    // Secondary key using field 1, ie the partyCode (aka ABN)
    SecondaryConfig secondaryConfig = new SecondaryConfig();
    secondaryConfig.setAllowCreate(true);
    secondaryConfig.setTransactional(true);
    secondaryConfig.setAllowPopulate(true);
    secondaryConfig.setSortedDuplicates(false);
    IDatabaseEntryFields entryFields = new DatabaseEntryFields(Integer.BYTES, IDatabaseEntryFields.NUL_TERMINATED);
    SecondaryKeyCreator keyCreator1 = new FieldedKeyCreator(entryFields, 1);
    secondaryConfig.setKeyCreator(keyCreator1);
    SecondaryDatabase secondaryDatabase = databaseEnvironment.openSecondaryDatabase(null, className + "_1", entityDatabase, secondaryConfig); 

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

         Transaction txn2 = databaseEnvironment.beginTransaction();
         try {
           data.setValue(line);
           
           entityDatabase.put(txn2, key, data);
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
    try (Cursor cursor = entityDatabase.openCursor (null, null)) {
      OperationStatus status = cursor.getFirst(null, data, LockMode.DEFAULT);
      while (status == OperationStatus.SUCCESS) {
        Party party = data.getValue();
        System.out.println(party);
        status = cursor.getNext(null, data, LockMode.DEFAULT);
      }
      cursor.close();
    }
    System.out.println();
    
    // Read the database: in secondary key order
    System.out.println("ABN key order:");
    try (SecondaryCursor cursor = secondaryDatabase.openCursor (null, null)) {
      OperationStatus status = cursor.getFirst(null, data, LockMode.DEFAULT);
      while (status == OperationStatus.SUCCESS) {
        Party party = data.getValue();
        System.out.println(party);
        status = cursor.getNext(null, data, LockMode.DEFAULT);
      }
      cursor.close();
    }
    System.out.println();
    
    secondaryDatabase.close();
    entityDatabase.close();
    
    timer.report();
  }
  
}
