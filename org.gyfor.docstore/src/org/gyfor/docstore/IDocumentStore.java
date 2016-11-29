package org.gyfor.docstore;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;

import org.gyfor.util.MimeType;

import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public interface IDocumentStore {

  public String importDocument (File file);
  
  public String importDocument (Path path);
  
  public String importDocument (URL url);
  
  public String importDocument(InputStream is, MimeType mimeType);
  
  public Document getDocument (String id);
  
  public void removeDocument (String id);

  public Path getViewImagePath(Document document);

  public Path getViewImagePath(String id, String extn);

//  public void saveViewImage(String id, BufferedImage image);

  public Path getViewHTMLPath(String id);

  public Path getSourcePath(String id);
  
  public Path getSourcePath(String id, String extn);
  
  public boolean isImageFile(String extn);
  
  public PrimaryIndex<String, Document> getPrimaryIndex();
  
  public SecondaryIndex<Date, String, Document> getImportDateIndex();

  public Path getBasePath();

}
