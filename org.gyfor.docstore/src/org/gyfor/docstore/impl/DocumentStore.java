package org.gyfor.docstore.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.gyfor.berkeleydb.DataStore;
import org.gyfor.docstore.Document;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.parser.impl.PDFToImage;
import org.gyfor.nio.SafeOutputStream;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.util.CRC64Digest;
import org.gyfor.util.CRC64DigestFactory;
import org.gyfor.util.Digest;
import org.gyfor.util.DigestFactory;
import org.gyfor.util.MimeType;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.objectplanet.image.PngEncoder;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class DocumentStore implements IDocumentStore {

  // This document store uses CRC64 digest
  private final DigestFactory digestFactory = new CRC64DigestFactory();

  @Configurable
  private Path baseDir = Paths.get(System.getProperty("user.home"), "/data");

  private DataStore dataStore;
  
  private PrimaryIndex<Long, Document> primaryIndex;
  private SecondaryIndex<Date, Long, Document> importDateIndex;
  
  private Path sourceDir;
  private Path imagesDir;
  
  @Reference
  public void setDataStore (DataStore dataStore) {
    this.dataStore = dataStore;
  }
  
  
  public void unsetDataStore (DataStore dataStore) {
    this.dataStore = null;
  }
  
  
  @Activate
  public void activate(ComponentContext context) {
    ComponentConfiguration.load(this, context);

    primaryIndex = dataStore.getPrimaryIndex(Long.class, Document.class);
    importDateIndex = dataStore.getSecondaryIndex(primaryIndex, Date.class, "importTime");
    
    try {
      sourceDir = baseDir.resolve("source");
      Files.createDirectories(sourceDir);
      imagesDir = baseDir.resolve("images");
      Files.createDirectories(imagesDir);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  @Override 
  public Path getSourcePath (Digest digest) {
    Document doc = primaryIndex.get(((CRC64Digest)digest).getLong());
    if (doc == null) {
      throw new IllegalArgumentException("No source file with digest: " + digest);
    }
    String extn = doc.getOriginExtension();
    return sourceDir.resolve(digest + extn);
  }

  
  @Override
  public Digest importDocument(File file) {
    return importDocument(file.toPath());
  }
  
  
  @Override
  public Digest importDocument(URL url) {
    String pathName = url.getPath();
    int n = pathName.lastIndexOf('.');
    if (n == -1) {
      throw new IllegalArgumentException("Source URL with no extension (ie no type)");
    }
    String extn = pathName.substring(n);
    String originName = new File(pathName).getName();
    
    Digest digest = digestFactory.getFileDigest(url);
    InputStream is;
    try {
      is = url.openStream();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return importDocument(digest, is, originName, extn);
  }
  
  
  @Override
  public Digest importDocument(Path path) {
    Digest digest = digestFactory.getFileDigest(path);
    if (primaryIndex.contains(((CRC64Digest)digest).getLong())) {
      // No need to import.  The file already exists.  The file is uniquely named by it's digest, 
      // so if it exists under that name, it exists and is current.  Size and timestamp do not 
      // need to be checked.
      return digest;
    }
    
    String pathName = path.toString();
    int n = pathName.lastIndexOf('.');
    if (n == -1) {
      throw new IllegalArgumentException("Source path with no extension (ie no type)");
    }
    String extn = pathName.substring(n);
    Path newSourcePath = sourceDir.resolve(digest + extn);
    
    // As a double check, check that the file does not exist
    if (!Files.exists(newSourcePath)) {
      // Copy the file
      try (SafeOutputStream targetOutputStream = new SafeOutputStream(newSourcePath)) {
        Files.copy(path, targetOutputStream);
        targetOutputStream.commit();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    Date lastModified = new Date(path.toFile().lastModified());
    completeImport (digest, newSourcePath, lastModified, path.getFileName().toString(), extn);

    return digest;
  }
  

  @Override
  public Digest importDocument(InputStream is, MimeType mimeType) {
    Digest digest = digestFactory.getInputStreamDigest(is);
    try {
      is.reset();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return importDocument(digest, is, null, mimeType.getExtension());
  }
  
  
  private Digest importDocument(Digest digest, InputStream is, String originName, String extn) {
    if (primaryIndex.contains(((CRC64Digest)digest).getLong())) {
      // No need to import.  The file already exists.  The file is uniquely named by it's digest, 
      // so if it exists under that name, it exists and is current.  Size and timestamp do not 
      // need to be checked.
      return digest;
    }

    Path newSourcePath = sourceDir.resolve(digest + extn);
    
    // As a double check, check that the file does not exist
    if (!Files.exists(newSourcePath)) {
      // Copy the file
      try (SafeOutputStream targetOutputStream = new SafeOutputStream(newSourcePath)) {
        byte[] buffer = new byte[4096];
        int n = is.read(buffer);
        while (n > 0) {
          targetOutputStream.write(buffer, 0, n);
          n = is.read(buffer);
        }
        targetOutputStream.commit();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    completeImport (digest, newSourcePath, null, originName, extn);
    return digest;
  }

  
  private void completeImport (Digest digest, Path sourcePath, Date originTime, String originName, String extn) {
    // Get a viewable image of the document (for overlaying with field information).
    // Image source files do not need a viewable image.
    if (!isImageFile(extn)) {
      writeViewImage (digest, sourcePath, extn);
    }
    
    // Write Document record
    Document document = new Document(((CRC64Digest)digest).getLong(), originTime, originName, extn);
    primaryIndex.put(document);
  }

  
  private static final String[] imageExtensions = {
      ".png",
      ".jpg",
      ".jpeg",
      ".tiff",
      ".gif",
      ".bmp",
      ".bpg",
      ".svg",
  };

  
  private static boolean isImageFile (String extn) {
    for (String ie : imageExtensions) {
      if (extn.equals(ie)) {
        return true;
      }
    }
    return false;
  }
  
  
  private void writeViewImage (Digest digest, Path file, String extn) {
    switch (extn) {
    case ".pdf" :
      PDFToImage.buildImageFile(digest, file, this);
      break;
    default :
      throw new RuntimeException("Cannot build view image for " + file + ": not supported");
    }
  }
  
  
  @Override 
  public Path getViewImagePath (Digest digest) {
    Document doc = primaryIndex.get(((CRC64Digest)digest).getLong());
    if (doc == null) {
      throw new IllegalArgumentException("No source file with digest: " + digest);
    }

    // If the origin extension is that of an image, the view image path is the source path
    String extn = doc.getOriginExtension();
    if (isImageFile(extn)) {
      return sourceDir.resolve(digest + extn);
    } else {
      return imagesDir.resolve(digest + ".png");
    }
  }
  

  @Override 
  public Path getViewImagePath (Digest digest, String extn) {
    if (isImageFile(extn)) {
      return sourceDir.resolve(digest + extn);
    } else {
      return imagesDir.resolve(digest + ".png");
    }
  }
  

  @Override
  public void saveViewImage (Digest digest, BufferedImage image) {
    Path imageFile = getViewImagePath(digest);
    PngEncoder pngEncoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR);
    try (FileOutputStream imageOut = new FileOutputStream(imageFile.toFile())) {
      pngEncoder.encode(image, imageOut);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public Path getViewHTMLPath(Digest digest) {
    Path htmlDir = baseDir.resolve("html");
    try {
      Files.createDirectories(htmlDir);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    return htmlDir.resolve(digest + ".html");
  }


  @Override
  public void removeDocument(Digest docDigest) {
    primaryIndex.delete(((CRC64Digest)docDigest).getLong());
  }


  @Override
  public Document getDocument(Digest docDigest) {
    return primaryIndex.get(((CRC64Digest)docDigest).getLong());
  }


  @Override
  public PrimaryIndex<Long, Document> getPrimaryIndex() {
    return primaryIndex;
  }


  @Override
  public SecondaryIndex<Date, Long, Document> getImportDateIndex() {
    return importDateIndex;
  }

}
