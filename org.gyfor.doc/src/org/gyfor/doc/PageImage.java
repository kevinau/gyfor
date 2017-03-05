package org.gyfor.doc;

import java.io.Serializable;


public class PageImage implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int imageIndex;
  private final int imageWidth;
  private final int imageHeight;
  
  
  public PageImage (int imageIndex, int imageWidth, int imageHeight) {
    this.imageIndex = imageIndex;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
  }
  
  
  public int getImageIndex () {
    return imageIndex;
  }
  
  public int getWidth () {
    return imageWidth;
  }
  
 
  public int getHeight () {
    return imageHeight;
  }
  
}
