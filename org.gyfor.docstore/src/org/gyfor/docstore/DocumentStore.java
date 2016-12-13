package org.gyfor.docstore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.gyfor.berkeleydb.DataStore;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.gyfor.docstore.parser.impl.ImageIO;
import org.gyfor.docstore.parser.impl.PDFBoxPDFParser;
import org.gyfor.docstore.parser.impl.TesseractImageOCR;
import org.gyfor.nio.SafeOutputStream;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.util.CRC64DigestFactory;
import org.gyfor.util.Digest;
import org.gyfor.util.DigestFactory;
import org.gyfor.util.MimeType;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true)
public class DocumentStore implements IDocumentStore {

  private Logger logger = LoggerFactory.getLogger(DocumentStore.class);
  
  // This document store uses the CRC64 digest to identify documents
  private final DigestFactory digestFactory = new CRC64DigestFactory();

  @Configurable
  private Path baseDir = Paths.get(System.getProperty("user.home"), "/data");

  private static final String IMAGES = "images";
  private static final String SOURCE = "source";
  private static final String THUMBS = "thumbs";
  
  private DataStore dataStore;
  
  private PrimaryIndex<String, Document> primaryIndex;
  private SecondaryIndex<Date, String, Document> importDateIndex;
  
  private Path sourceDir;
  private Path imagesDir;
  private Path thumbsDir;
  
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

    primaryIndex = dataStore.getPrimaryIndex(String.class, Document.class);
    importDateIndex = dataStore.getSecondaryIndex(primaryIndex, Date.class, "importTime");
    
    try {
      sourceDir = baseDir.resolve(SOURCE);
      Files.createDirectories(sourceDir);
      imagesDir = baseDir.resolve(IMAGES);
      Files.createDirectories(imagesDir);
      thumbsDir = baseDir.resolve(THUMBS);
      Files.createDirectories(thumbsDir);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  @Override 
  public Path getSourcePath (String id) {
    Document doc = primaryIndex.get(id);
    if (doc == null) {
      throw new IllegalArgumentException("No source file with id: " + id);
    }
    String extn = doc.getOriginExtension();
    return sourceDir.resolve(id + extn);
  }

  
  @Override 
  public Path getSourcePath (String id, String extn) {
    return sourceDir.resolve(id + extn);
  }

  
  @Override
  public String importDocument(URL url) {
    logger.info("Importing document from URL {}", url);
    String pathName = url.getPath();
    int n = pathName.lastIndexOf('.');
    if (n == -1) {
      throw new IllegalArgumentException("Source URL with no extension (ie no type)");
    }
    String extn = pathName.substring(n).toLowerCase();
    String originName = new File(pathName).getName();
    
    Digest digest = digestFactory.getFileDigest(url);
    String id = digest.toString();
    InputStream is;
    try {
      is = url.openStream();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return importDocument(id, is, originName, extn);
  }
  
  
  @Override
  public String importDocument(File file) {
    return importDocument(file.toPath());
  }
  
  
  @Override
  public String importDocument(Path path) {
    Digest digest = digestFactory.getFileDigest(path);
    String id = digest.toString();
    
    if (primaryIndex.contains(id)) {
      // No need to import.  The file already exists.  The file is uniquely named by it's id, 
      // so if it exists under that name, it exists and is current.  Size and timestamp do not 
      // need to be checked.
      return id;
    }
    
    logger.info("Importing document from file/path {}", path);
    String pathName = path.toString();
    int n = pathName.lastIndexOf('.');
    if (n == -1) {
      throw new IllegalArgumentException("Source path with no extension (ie no type)");
    }
    String extn = pathName.substring(n).toLowerCase();
    Path newSourcePath = sourceDir.resolve(id + extn);
    
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
    completeImport (id, newSourcePath, lastModified, path.getFileName().toString(), extn);

    return id;
  }
  

  @Override
  public String importDocument(InputStream is, MimeType mimeType) {
    Digest digest = digestFactory.getInputStreamDigest(is);
    String id = digest.toString();
    
    try {
      is.reset();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return importDocument(id, is, null, mimeType.getExtension());
  }
  
  
  private String importDocument(String id, InputStream is, String originName, String extn) {
    if (primaryIndex.contains(id)) {
      // No need to import.  The file already exists.  The file is uniquely named by it's id, 
      // so if it exists under that name, it exists and is current.  Size and timestamp do not 
      // need to be checked.
      return id;
    }

    Path newSourcePath = sourceDir.resolve(id + extn);
    
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
    
    completeImport (id, newSourcePath, null, originName, extn);
    return id;
  }

  
  private void completeImport (String id, Path sourcePath, Date originTime, String originName, String extn) {
    Path path = getSourcePath(id, extn);

    logger.info("Parsing {} to extact textual contents", path.getFileName());
    IDocumentContents docContents;
    if (isImageFile(extn)) {
      IImageParser imageParser = new TesseractImageOCR();
      docContents = imageParser.parse(id, 0, path);
      
      // Write a thumbnail version of the image
      BufferedImage image = ImageIO.getImage(path);
      Path thumbsFile = getThumbsImagePath(id);
      ImageIO.writeThumbnail(image, thumbsFile);
    } else {
      switch (extn) {
      case ".pdf" :
        IImageParser imageParser = new TesseractImageOCR();
        IPDFParser pdfParser = new PDFBoxPDFParser(imageParser);
        docContents = pdfParser.parse(id, path, 150, this);
        break;
      default :
        throw new RuntimeException("File type: " + path + " not supported");
      }
    }

    // Get a viewable image of the document (for overlaying with field information).
    // Image source files do not need a viewable image.
    ////if (!isImageFile(extn)) {
    ////  writeViewImage (id, sourcePath, extn);
    ////}
    
    // Write Document record
    Document document = new Document(id, originTime, originName, extn, docContents);
    primaryIndex.put(document);
    logger.info("Import complete: {} -> {}", originName, id);
    
    // For debugging
    Document d = primaryIndex.get(id);
    IDocumentContents dc = d.getContents();
    System.out.println(">>>> " + dc);
    for (ISegment seg : dc.getSegments()) {
      System.out.println(">>>> " + seg);
    }
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

  
  @Override
  public boolean isImageFile (String extn) {
    for (String ie : imageExtensions) {
      if (extn.equals(ie)) {
        return true;
      }
    }
    return false;
  }
  
  
//  private void writeViewImage (String id, Path file, String extn) {
//    switch (extn) {
//    case ".pdf" :
//      PDFToImage.buildImageFile(id, file, this);
//      break;
//    default :
//      throw new RuntimeException("Cannot build view image for " + file + ": not supported");
//    }
//  }
  
  @Override
  public Path getBasePath () {
    return baseDir;
  }
  
  
  @Override 
  public Path getViewImagePath (Document doc) {
    // If the origin extension is that of an image, the view image path is the source path
    String extn = doc.getOriginExtension();
    if (isImageFile(extn)) {
      return sourceDir.resolve(doc.getId() + extn);
    } else {
      return imagesDir.resolve(doc.getId() + ".png");
    }
  }
  

  @Override
  public String webViewImagePath (String id, String extn, int page) {
    String base = "/" + IMAGES + "/" + id;
    if (page > 0) {
      base += ".p" + page;
    }
    if (isImageFile(extn)) {
      return base + extn;
    } else {
      return base + ".png";
    }
  }

  
  @Override 
  public String webThumbsImagePath (String id) {
    return "/" + THUMBS + "/" + id + ".png";
  }
  

  @Override 
  public String webSourcePath (Document doc) {
    return "/" + SOURCE + "/" + doc.getId() + doc.getOriginExtension();
  }
  

  @Override 
  public Path newViewImagePath (String id, int page) {
    if (page == 0) {
      return imagesDir.resolve(id + ".png");
    } else {
      return imagesDir.resolve(id + ".p" + page + ".png");
    }
  }
  

  @Override 
  public Path getThumbsImagePath (String id) {
    return thumbsDir.resolve(id + ".png");
  }
  

//  @Override 
//  public Path getViewImagePath (String id, String extn) {
//    if (isImageFile(extn)) {
//      return sourceDir.resolve(id + extn);
//    } else {
//      return imagesDir.resolve(id + ".png");
//    }
//  }
  

//  @Override
//  public void saveViewImage (String id, BufferedImage image) {
//    Path imageFile = getViewImagePath(id);
//    PngEncoder pngEncoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR);
//    try (FileOutputStream imageOut = new FileOutputStream(imageFile.toFile())) {
//      pngEncoder.encode(image, imageOut);
//    } catch (IOException ex) {
//      throw new RuntimeException(ex);
//    }
//  }


  @Override
  public Path getViewHTMLPath(String id) {
    Path htmlDir = baseDir.resolve("html");
    try {
      Files.createDirectories(htmlDir);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    return htmlDir.resolve(id + ".html");
  }


  @Override
  public void removeDocument(String id) {
    primaryIndex.delete(id);
  }


  @Override
  public Document getDocument(String id) {
    return primaryIndex.get(id);
  }


  @Override
  public PrimaryIndex<String, Document> getPrimaryIndex() {
    return primaryIndex;
  }


  @Override
  public SecondaryIndex<Date, String, Document> getImportDateIndex() {
    return importDateIndex;
  }

}
