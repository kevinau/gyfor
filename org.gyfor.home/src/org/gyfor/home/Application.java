package org.gyfor.home;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;


@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class Application implements IApplication {

  @Configurable
  private String id = "pennyledger";
  
  @Configurable
  private Path parentDir = Paths.get(System.getProperty("user.home"));

  @Configurable
  private Path baseDir = null;
  

  @Activate
  protected void activate(ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    if (baseDir == null) {
      baseDir = parentDir.resolve(id);
    }
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Path getBaseDir() {
    return baseDir;
  }
  
}
