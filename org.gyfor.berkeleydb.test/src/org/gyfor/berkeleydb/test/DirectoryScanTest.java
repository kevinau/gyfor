package org.gyfor.berkeleydb.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

import org.gyfor.berkeleydb.DataStore;
import org.gyfor.util.CRC64DigestFactory;
import org.gyfor.util.Digest;
import org.gyfor.util.DigestFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.doc.UnclassifiedDocument;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;

@Component
public class DirectoryScanTest {

  private DigestFactory hashFactory = new CRC64DigestFactory();
  
  private DataStore entityStore;
  
  
  @Reference
  public void setDataStore (DataStore entityStore) {
    this.entityStore = entityStore;
  }
  
  
  public void unsetDataStore (DataStore entityStore) {
    this.entityStore = null;
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    Bundle bundle = bundleContext.getBundle();
    
    PrimaryIndex<String, UnclassifiedDocument> index = entityStore.getPrimaryIndex(String.class, UnclassifiedDocument.class);
    
    Enumeration<URL> urls = bundle.findEntries("/docs/ASX", "*", false);
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      Digest hash = hashFactory.getFileDigest(url);
      
      long lastModified;
      try {
        lastModified = url.openConnection().getLastModified();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      Date originDate = new Date(lastModified);
      //LocalDate originDate = modified.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      
      String originName = new File(url.getPath()).getName();
      String originMimeType = null;
      int n = originName.lastIndexOf('.');
      if (n >= 0) {
        originMimeType = originName.substring(n);
      }
      
      UnclassifiedDocument undoc = new UnclassifiedDocument(hash.toString(), originDate, originName, originMimeType);
      index.put(undoc);
      
      System.out.println(hash + ": " + originName + " " + originDate + " " + originMimeType);
    }
    
    EntityCursor<UnclassifiedDocument> indexCursor = index.entities();
    try {
      for (UnclassifiedDocument doc : indexCursor) {
        System.out.println("=============== " + doc);
      }
    } finally {
      indexCursor.close();
    } 
  }
  
}
