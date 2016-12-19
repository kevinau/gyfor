package org.gyfor.docstore;

import java.io.Serializable;

import com.sleepycat.persist.model.Persistent;


@Persistent
public class PageImage implements Serializable {

  private static final long serialVersionUID = 1L;

  private int imageWidth;
  private int imageHeight;
  
  
  public PageImage () {
  }
  
  
  public PageImage (int imageWidth, int imageHeight) {
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
  }
  
  
  public int getWidth () {
    return imageWidth;
  }
  
 
  public int getHeight () {
    return imageHeight;
  }
  
}
