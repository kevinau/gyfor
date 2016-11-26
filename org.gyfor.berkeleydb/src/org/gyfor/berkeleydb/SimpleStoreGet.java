package org.gyfor.berkeleydb;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;


public class SimpleStoreGet {

  private static final File envHome = new File(System.getProperty("user.home"), "data");

  private Environment envmnt;
  private EntityStore store;
  private EntityDAO<SimpleEntity> sda;


  // The setup() method opens the environment and store
  // for us.
  public void setup() throws DatabaseException {

    EnvironmentConfig envConfig = new EnvironmentConfig();
    StoreConfig storeConfig = new StoreConfig();

    envConfig.setAllowCreate(true);
    storeConfig.setAllowCreate(true);

    // Open the environment and entity store
    envmnt = new Environment(envHome, envConfig);
    store = new EntityStore(envmnt, "EntityStore", storeConfig);
  }


  // Close our environment and store.
  public void shutdown() throws DatabaseException {

    store.close();
    envmnt.close();
  }


  // Retrieve some SimpleEntityClass objects from the store.
  private void run() throws DatabaseException {

    setup();

    // Open the data accessor. This is used to store
    // persistent objects.
    sda = new EntityDAO<>(store, SimpleEntity.class);

    // Instantiate and store some entity classes
    SimpleEntity sec1 = sda.get("keyone");
    SimpleEntity sec2 = sda.get("keytwo");

    SimpleEntity sec4 = sda.getViaSecondary("skeythree");

    System.out.println("sec1: " + sec1.getPKey());
    System.out.println("sec2: " + sec2.getPKey());
    System.out.println("sec4: " + sec4.getPKey());

    shutdown();
  }


  // main
  public static void main(String args[]) {
    SimpleStoreGet ssg = new SimpleStoreGet();
    try {
      ssg.run();
    } catch (DatabaseException dbe) {
      System.err.println("SimpleStoreGet: " + dbe.toString());
      dbe.printStackTrace();
    } catch (Exception e) {
      System.out.println("Exception: " + e.toString());
      e.printStackTrace();
    }
    System.out.println("All done.");
  }

}
