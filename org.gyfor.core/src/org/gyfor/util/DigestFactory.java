package org.gyfor.util;

import java.io.InputStream;
import java.nio.file.Path;

public interface HashFactory {
  
  public Hash getFileDigest (Path path);
  
  public Hash getInputStreamDigest (InputStream fis);
  
  public Hash getObjectDigest (Object obj);

}
