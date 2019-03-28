package org.leadlightdesigner;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

public class CachedImage {

  private static Map<String, Image> images = new HashMap<>();
  
  public static Image getImage(Widget widget, String fileName) {
    synchronized (images) {
      Image image = images.get(fileName);
      if (image == null) {
        String resource = ImageRegistry.class.getResource("/").getPath() + fileName;
        image = new Image(widget.getDisplay(), resource);
        images.put(fileName, image);
      }
      return image;
    }
  }
  
}
