package org.gyfor.docstore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;

import org.gyfor.util.Digest;
import org.gyfor.util.MimeType;

import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

public interface IDocumentStore {

  public Digest importDocument (File file);
  
  public Digest importDocument (Path path);
  
  public Digest importDocument (URL url);
  
  public Digest importDocument(InputStream is, MimeType mimeType);
  
  public Document getDocument (Digest docDigest);
  
  public void removeDocument (Digest docDigest);

  public Path getViewImagePath(Digest digest);

  public Path getViewImagePath(Digest digest, String extn);

  public void saveViewImage(Digest digest, BufferedImage image);

  public Path getViewHTMLPath(Digest id);

  public Path getSourcePath(Digest digest);
  
  public PrimaryIndex<Long, Document> getPrimaryIndex();
  
  public SecondaryIndex<Date, Long, Document> getImportDateIndex();
  
}
