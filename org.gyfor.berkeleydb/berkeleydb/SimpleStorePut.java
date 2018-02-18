package org.gyfor.berkeleydb;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;


public class SimpleStorePut {

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


  // Populate the entity store
  private void run() throws DatabaseException {

    setup();

    // Open the data accessor. This is used to store
    // persistent objects.
    sda = new EntityDAO<>(store, SimpleEntity.class);

    // Instantiate and store some entity classes
    SimpleEntity sec1 = new SimpleEntity();
    SimpleEntity sec2 = new SimpleEntity();
    SimpleEntity sec3 = new SimpleEntity();
    SimpleEntity sec4 = new SimpleEntity();
    SimpleEntity sec5 = new SimpleEntity();

    sec1.setPKey("keyone");
    sec1.setSKey("skeyone");

    sec2.setPKey("keytwo");
    sec2.setSKey("skeyone");

    sec3.setPKey("keythree");
    sec3.setSKey("skeytwo");

    sec4.setPKey("keyfour");
    sec4.setSKey("skeythree");

    sec5.setPKey("keyfive");
    sec5.setSKey("skeyfour");

    sda.put(sec1);
    sda.put(sec2);
    sda.put(sec3);
    sda.put(sec4);
    sda.put(sec5);

    shutdown();
  }


  // main
  public static void main(String args[]) {
    SimpleStorePut ssp = new SimpleStorePut();
    try {
      ssp.run();
    } catch (DatabaseException dbe) {
      System.err.println("SimpleStorePut: " + dbe.toString());
      dbe.printStackTrace();
    } catch (Exception e) {
      System.out.println("Exception: " + e.toString());
      e.printStackTrace();
    }
    System.out.println("All done.");
  }
}
