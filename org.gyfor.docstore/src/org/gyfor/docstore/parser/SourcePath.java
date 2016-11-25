package org.gyfor.docstore.parser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gyfor.util.CRC64DigestFactory;
import org.gyfor.util.Digest;

@Deprecated
public class SourcePath {

  private static final String baseDir = "C:/PennyLedger";
  
  private final Path sourcePath;
  
  
  public SourcePath (String sourcePathName) {
    this.sourcePath = Paths.get(sourcePathName);
  }
  
  
  public SourcePath (Path sourcePath) {
    this.sourcePath = sourcePath;
  }
  
  
  public static Path createDigestNamed (Path sourcePath) {
    Digest digest = new CRC64DigestFactory().getFileDigest(sourcePath);
    
    String fileName = sourcePath.getFileName().toString();
    String extn;
    int n = fileName.lastIndexOf('.');
    if (n == -1) {
      extn = "";
    } else {
      extn = fileName.substring(n);
    }
    return Paths.get(baseDir, digest.toString() + extn);
  }

  
  public Path toPath () {
    return sourcePath;
  }
  
  
  public File toFile () {
    return sourcePath.toFile();
  }
  
  
  public String getFileName () {
    return sourcePath.getFileName().toString();
  }
  
  
  private Path replaceExtn (String extn) {
    String fileName = sourcePath.toString();
    int n = fileName.lastIndexOf('.');
    if (n == -1) {
      fileName += extn;
    } else {
      fileName = fileName.substring(0,  n) + extn;
    }
    return Paths.get(fileName);
  }
  
  
  public Path getViewImagePath () {
    return replaceExtn(".png");  
  }


  public Path getViewHTMLPath () {
    return replaceExtn(".html");  
  }


  public Path getDataPath () {
    return replaceExtn(".data");  
  }


  @Deprecated
  public Path getOCRImagePath () {
    return replaceExtn(".ocr.png");  
  }


  @Deprecated
  public Path getOCRImagePath (int page, int image) {
    return replaceExtn(".p" + page + "i" + image + ".png");  
  }


  @Deprecated
  public Path getBasePath () {
    return replaceExtn("");  
  }
  
  
  @Deprecated
  public Path getHTMLPath () {
    return replaceExtn(".html");
  }


  public Path getContentsPath () {
    return replaceExtn(".contents");
  }
  
  
  @Override
  public String toString () {
    return sourcePath.toString();
  }

}
