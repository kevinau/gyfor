package org.plcore.docstore;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.plcore.srcdoc.SourceDocument;
import org.plcore.srcdoc.SourceReference;
import org.plcore.util.MimeType;


public interface IDocumentStore {

  public static final int IMAGE_RESOLUTION = 360;
  public static final double IMAGE_SCALE = 0.3;
  
  public void addDocumentStoreListener(DocumentStoreListener x);
  
  public void removeDocumentStoreListener(DocumentStoreListener x);
  
  public String importDocument (File file);
  
  public String importDocument (Path path);
  
  public String importDocument (URL url);
  
  public String importDocument(InputStream is, MimeType mimeType);
  
  public SourceDocument getDocument (String hashCode);
  
  public List<SourceReference> getAllDocuments();

  public void removeDocument (SourceDocument document);

  public Path getViewImagePath(SourceDocument document);

  public Path newViewImagePath(String hashCode, int page);
  
  public Path getThumbsImagePath(String hashCode);

//  public Path getViewImagePath(String hashCode, String extn);

//  public void saveViewImage(String hashCode, BufferedImage image);

  public Path getViewHTMLPath(String hashCode);

  public Path getSourcePath(String hashCode, String extn);
  
  public boolean isImageFile(String extn);
  
  public Path getBasePath();

  public String webViewImagePath(String hashCode, String extn, int page);
  
  public String webThumbsImagePath(String hashCode);

  public String webSourcePath(SourceDocument doc);

}
