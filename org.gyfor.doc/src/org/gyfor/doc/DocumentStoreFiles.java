package org.gyfor.doc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gyfor.home.IApplication;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true)
public class DocumentStoreFiles {
  
  private Logger logger = LoggerFactory.getLogger(DocumentStoreFiles.class);

  @Reference
  public IApplication application;
  
  private Path baseDir;

  
  @Activate
  public void activate(ComponentContext context) {
    baseDir = application.getBaseDir();
    logger.info("Document store base directory set to: {}", baseDir);
  }

  
  public Path init(String name) {
    Path path = baseDir.resolve(name);
    try {
      Files.createDirectories(path);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return path;
  }
  
  
  public Path resolve(String name) {
    return baseDir.resolve(name);
  }

}
