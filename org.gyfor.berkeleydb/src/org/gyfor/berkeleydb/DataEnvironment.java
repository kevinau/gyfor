package org.gyfor.berkeleydb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;


@Component(service = DataEnvironment.class)
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
  
}

