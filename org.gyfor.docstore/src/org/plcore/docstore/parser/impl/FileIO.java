package org.plcore.docstore.parser.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.plcore.nio.SafeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileIO {

  private static final Logger logger = LoggerFactory.getLogger(FileIO.class);
  
  public static boolean conditionallyCopyFile (Path sourcePath, Path targetPath) {
    boolean needToCopy;
    
    if (Files.exists(targetPath)) {
      long sourceLength;
      long hashNamedLength;
      try {
        sourceLength = Files.size(sourcePath);
        hashNamedLength = Files.size(targetPath);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      if (sourceLength != hashNamedLength) {
        needToCopy = true;
      } else {
        // targetPath file exists and is the right length, so assume all is good.
        needToCopy = false;
      }
    } else {
      needToCopy = true;
    }
    
    if (needToCopy) {
      logger.info("Copying {} to {}", sourcePath, targetPath.getFileName());
      try (OutputStream targetOutputStream = new SafeOutputStream(targetPath)) {
        Files.copy(sourcePath, targetOutputStream);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      logger.info("No file copy required.  {} is already a copy of {}", targetPath.getFileName(), sourcePath);
    }
    
    return needToCopy;
  }
  
  
//  public static int conditionallyCopyFiles (List<Path> files, List<Path> targetNamedFiles) {
//    int copied = 0;
//    for (Path path : files) {
//      Path hashNamedPath = SourcePath.createDigestNamed(path);
//      boolean copyPerformed = conditionallyCopyFile (path, hashNamedPath);
//      if (copyPerformed) {
//        copied++;
//      }
//      if (targetNamedFiles != null) {
//        targetNamedFiles.add(hashNamedPath);
//      }
//    }
//    return copied;
//  }
//
//  
//  public static int conditionallyCopyFiles (List<Path> files) {
//    return  conditionallyCopyFiles (files, null);
//  }

}
