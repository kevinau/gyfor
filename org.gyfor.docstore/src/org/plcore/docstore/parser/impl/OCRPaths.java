package org.plcore.docstore.parser.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

class OCRPaths {

  private static final String tmpDir = System.getProperty("java.io.tmpdir");
  
  static Path getOCRImagePath (String id) {
    return Paths.get(tmpDir, id + ".ocr.png");  
  }


  static Path getOCRImagePath (String id, int page) {
    return Paths.get(tmpDir, id + ".p" + page + ".png");  
  }


  static Path getOCRImagePath (String id, int page, int image) {
    return Paths.get(tmpDir, id + ".p" + page + "i" + image + ".png");  
  }


  static Path getBasePath (String id) {
    return Paths.get(tmpDir, id);  
  }
  
  
  static Path getHocrPath (String id) {
    return Paths.get(tmpDir, id + ".hocr");
  }

}
