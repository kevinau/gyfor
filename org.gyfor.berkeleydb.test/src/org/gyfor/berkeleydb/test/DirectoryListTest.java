package org.gyfor.berkeleydb.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;

import org.gyfor.berkeleydb.DataStore;
import org.gyfor.docstore.Document;
import org.gyfor.util.CRC64DigestFactory;
import org.gyfor.util.Digest;
import org.gyfor.util.DigestFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;

@Component
public class DirectoryListTest {

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
    
    PrimaryIndex<String, FileDetails> index = entityStore.getPrimaryIndex(String.class, FileDetails.class);
    
    Enumeration<URL> urls = bundle.findEntries("/docs/ASX", "*", false);
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      Digest hash = hashFactory.getFileDigest(url);
      
      long lastModified;
      int fileLength;
      try {
        lastModified = url.openConnection().getLastModified();
        fileLength = url.openConnection().getContentLength();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      Date originDate = new Date(lastModified);
      String originName = new File(url.getPath()).getName();

      FileDetails details = new FileDetails(originName, originDate, fileLength);
      System.out.println(details);
      index.put(details);
    }
    
    EntityCursor<FileDetails> indexCursor = index.entities();
    try {
      for (FileDetails doc : indexCursor) {
        System.out.println("=============== " + doc);
      }
    } finally {
      indexCursor.close();
    } 
  }
  
}
