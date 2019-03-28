package org.leadlightdesigner;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

public class ImageFileType implements IManagedFileType {

  private static final String[] imageSuffixes = ImageIO.getReaderFileSuffixes();

  @Override
  public Image getFileIcon(Widget widget) {
    return CachedImage.getImage(widget, "icons/obj16/camera.png");
  }
  

  @Override
  public String getFilterExtensions() {
    String imageGlobs = "*" + imageSuffixes[0];
    for (int i = 1; i < imageSuffixes.length; i++) {
      imageGlobs += ";*" + imageSuffixes[i];
    }
    return imageGlobs;
  }

  
  @Override
  public String getFilterNames() {
    return "Image files";
  }

  
  @Override
  public String getDescription() {
    return "image file";
  }

}
