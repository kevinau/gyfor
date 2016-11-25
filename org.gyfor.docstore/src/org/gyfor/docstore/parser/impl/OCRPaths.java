package org.gyfor.docstore.parser.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.gyfor.util.Digest;

class OCRPaths {

  private static final String tmpDir = System.getProperty("java.io.tmpdir");
  
  static Path getOCRImagePath (Digest digest) {
    return Paths.get(tmpDir, digest + ".ocr.png");  
  }


  static Path getOCRImagePath (Digest digest, int page, int image) {
    return Paths.get(tmpDir, digest + ".p" + page + "i" + image + ".png");  
  }


  static Path getBasePath (Digest digest) {
    return Paths.get(tmpDir, digest.toString());  
  }
  
  
  static Path getHTMLPath (Digest digest) {
    return Paths.get(tmpDir, digest + ".html");
  }

}
