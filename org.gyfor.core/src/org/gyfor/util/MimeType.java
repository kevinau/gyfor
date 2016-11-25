package org.gyfor.util;

import java.util.Arrays;

public class MimeType {
 
  private final String mimeType;
  
  private final String[] extensions;


  MimeType (String mimeType, String... extensions) {
    if (extensions.length == 0) {
      throw new IllegalArgumentException("A mime type must have one or more extensions");
    }
    this.mimeType = mimeType;
    this.extensions = extensions;
  }
  
  
  public String getExtension () {
    return extensions[0];
  }

  
  public String getMimeType () {
    return mimeType;
  }
  

  @Override
  public String toString() {
    return "MimeType [" + mimeType + ", " + Arrays.toString(extensions) + "]";
  }
  
}
