package org.gyfor.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface DigestFactory {
  
  public Digest getFileDigest (File file);
  
  public Digest getFileDigest (Path path);
  
  public Digest getFileDigest (URL url);
  
  public Digest getInputStreamDigest (InputStream fis);
  
  public Digest getObjectDigest (Object obj);

}
