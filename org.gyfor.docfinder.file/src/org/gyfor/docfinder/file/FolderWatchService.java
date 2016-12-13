package org.gyfor.docfinder.file;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.regex.Pattern;

import org.gyfor.nio.DirectoryWatcher;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.value.ExistingDirectory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.gyfor.docstore.IDocumentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service=FolderWatchService.class, configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class FolderWatchService {

  private final Logger logger = LoggerFactory.getLogger(FolderWatchService.class);
  
  @Configurable
  private ExistingDirectory watchDir;

  @Configurable
  private Pattern pattern = Pattern.compile(".");
  
  private IDocumentStore docStore;
  
  private DirectoryWatcher watchService;
  
  
  @Reference
  public void setDocumentStore (IDocumentStore docStore) {
    this.docStore = docStore;
  }

  
  public void unsetDocumentStore (IDocumentStore docStore) {
    this.docStore = null;
  }
  
  
  @Activate
  protected void activate (ComponentContext context) {
    ComponentConfiguration.load(this, context);
    
    logger.info("Watching {} for new files (for importing into the document store)", watchDir);

    // Create a processor to import the documents found in the download directory
    DirectoryWatcher.IProcessor processor = new DirectoryWatcher.IProcessor() {
      @Override
      public void process(Path path, Kind<?> kind) {
        logger.info("Found {} {} for adding to document store", kind, path);
        docStore.importDocument(path);
      }      
    };
    
    watchService = new DirectoryWatcher(watchDir, pattern, processor);
    watchService.registerDirectory(watchDir);
    
    // Start watching the directory in a separate thread.
    new Thread() {
      @Override
      public void run() {
        watchService.start();
      }
    }.start();
    
    // Fire off the processor for existing files
    watchService.queueExistingFiles();
  }
  
  
  @Deactivate
  protected void deactivate () {
    watchService.close();
    logger.info("No longer watching {} for new files", watchDir);
  }
  
}
