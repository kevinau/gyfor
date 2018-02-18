package org.gyfor.berkeleydb;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;


public class Test {

  private final boolean readOnly = false;
  private final File envHome = new File(System.getProperty("user.home"), "data");
  
  private Environment myEnv;
  private EntityStore store;

  public void setup() {
    try {
      EnvironmentConfig myEnvConfig = new EnvironmentConfig();
      StoreConfig storeConfig = new StoreConfig();

      myEnvConfig.setAllowCreate(!readOnly);
      storeConfig.setAllowCreate(!readOnly);

      // Open the environment and entity store
      myEnv = new Environment(envHome, myEnvConfig);
      store = new EntityStore(myEnv, "EntityStore", storeConfig);
    } catch (DatabaseException dbe) {
      System.err.println("Error opening environment and store: " + dbe.toString());
      System.exit(-1);
    }
  }


  public void shutdown() {
    if (store != null) {
      try {
        store.close();
      } catch (DatabaseException dbe) {
        System.err.println("Error closing store: " + dbe.toString());
        System.exit(-1);
      }
    }

    if (myEnv != null) {
      try {
        // Finally, close environment.
        myEnv.close();
      } catch (DatabaseException dbe) {
        System.err.println("Error closing MyDbEnv: " + dbe.toString());
        System.exit(-1);
      }
    }
  }
  
  
  public static void main (String[] args) {
    Test test = new Test();
    System.out.println(test.envHome.getAbsolutePath());
    test.setup();
    test.shutdown();
  }
}
