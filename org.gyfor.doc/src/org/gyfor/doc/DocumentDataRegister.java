package org.gyfor.doc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;


@Component(service=DocumentDataRegister.class, configurationPolicy=ConfigurationPolicy.IGNORE)
public class DocumentDataRegister {

  /**
   * The name of the document data directory within the document store base.
   */
  private static final String DATA = "data";
  
  private static final String DATA_EXTN = ".dat";
  
  
  private Path dataDir;
  
  
  @Reference
  private DocumentStoreFiles docStoreFiles;
  
  
  @Activate
  private void activate(ComponentContext context) {
    dataDir = docStoreFiles.init(DATA);
  }
  
  
  private static class MapValue {
    private final DocumentData data;
    private final AtomicInteger use;
    
    private MapValue(DocumentData data) {
      this.data = data;
      this.use = new AtomicInteger(0);
    }
    
    private void incrementUse() {
      use.incrementAndGet();
    }

    private int decrementUse() {
      return use.decrementAndGet();
    }
  }
  
  
  private final Map<String, MapValue> dataMap = new HashMap<>();
  
  
  public DocumentData getDocumentData(String hashCode, DocumentContents docContents) {
    MapValue v;
    synchronized (dataMap) {
      v = dataMap.get(hashCode);
      if (v == null) {
        Path dataFile = dataDir.resolve(hashCode + DATA_EXTN);
        DocumentData d = DocumentData.load(dataFile);
        if (d == null) {
          // No document data file is present
          d = new DocumentData();
          d.save(dataFile);
        }
        v = new MapValue(d);
        dataMap.put(hashCode, v);
      }
    }
    v.incrementUse();
    return v.data;
  }
  
  
  public void returnDocumentData(String hashCode) {
    synchronized (dataMap) {
      MapValue v = dataMap.get(hashCode);
      if (v == null) {
        throw new IllegalArgumentException("Document " + hashCode + " not in registry");
      }
      int use = v.decrementUse();
      if (use == 0) {
        dataMap.remove(hashCode);
      }
    }
  }

}
