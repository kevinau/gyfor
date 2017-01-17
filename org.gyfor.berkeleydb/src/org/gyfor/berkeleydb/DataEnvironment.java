package org.gyfor.berkeleydb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;


@Component(service = DataEnvironment.class, immediate = true)
public class DataEnvironment {

  private Logger logger = LoggerFactory.getLogger(DataEnvironment.class);
  
  @Configurable
  private String envHome = System.getProperty("user.home") + "/data/berkeleydb";
  
  private Environment envmnt;

  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    logger.info("activate: envHome {}", envHome);
    
    // Create directory if it does not exist
    Path envPath = Paths.get(envHome);
    try {
      Files.createDirectories(envPath);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    
    // Set up the environment
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setAllowCreate(true);
    envConfig.setTransactional(true);

    // Open the environment
    File envHomeFile = new File(envHome);
    envmnt = new Environment(envHomeFile, envConfig);
  }
  
  
  @Deactivate
  public void deactivate () {
    logger.info("deactivate");
    envmnt.close();
  }
  
  
  public EntityStore newEntityStore (String storeName, StoreConfig storeConfig) {
    return new EntityStore(envmnt, "EntityStore", storeConfig);
  }
  
  
  public Database openDatabase (Transaction trans, String name, DatabaseConfig dbConfig) {
    return envmnt.openDatabase(trans, name, dbConfig);
  }
  
  
  public DataTable openTable (IPlanContext planEnvmt, Class<?> klass) {
    return openTable(planEnvmt, klass, false);
  }
  
  
  public DataTable openTable (IPlanContext planEnvmt, Class<?> klass, boolean writeable) {
    IEntityPlan<?> entityPlan = planEnvmt.getEntityPlan(klass);
    return openTable(entityPlan, writeable);
  }
  
  
  public DataTable openTable (IEntityPlan<?> entityPlan, boolean writeable) {
    return new DataTable(this, entityPlan, writeable);
  }
  
  
  public SecondaryDatabase openSecondaryDatabase (Transaction trans, String name, Database primaryDatabase, SecondaryConfig secondaryConfig) {
    return envmnt.openSecondaryDatabase(trans, name, primaryDatabase, secondaryConfig);
  }
  
  
  public Transaction beginTransaction () {
    return envmnt.beginTransaction(null, null);
  }

  
  public Transaction beginTransaction (Transaction trans, TransactionConfig transConfig) {
    return envmnt.beginTransaction(trans, transConfig);
  }
  
  
  public void truncateDatabase (String name) {
    String prefix = name + "_";
    
    Transaction txn = beginTransaction();
    List<String> names = envmnt.getDatabaseNames();
    for (String n : names) {
      if (n.equals(name) || n.startsWith(prefix)) {
        try {
          envmnt.truncateDatabase(txn, n, false);
          logger.info("Database truncated: {}", n);
        } catch (DatabaseNotFoundException ex) {
          // Do nothing if the database cannot be found
        } catch (Exception ex) {
          // But catch any other errors
          txn.abort();
          throw ex;
        }
      }
    }
    txn.commit();
  }
  
}

