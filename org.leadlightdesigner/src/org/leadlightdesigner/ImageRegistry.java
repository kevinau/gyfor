package org.leadlightdesigner;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

public class ImageRegistry {

  private static final Object obj = new Object();
  
  private static volatile Image folderImage = null;
  private static volatile Image cameraImage = null;
  private static volatile Image designImage = null;
  
  
  private static Image getImage (Widget widget, String name) {
    String resource = ImageRegistry.class.getResource("/").getPath() + name;
    Image image =  new Image(widget.getDisplay(), resource);
    return image;
  }
  
  
  public static Image getFolderImage(Widget widget) {
    if (folderImage == null) {
      synchronized (obj) {
        if (folderImage == null) {
          folderImage = getImage(widget, "icons/obj16/fldr_obj.png");
        }
      }
    }
    return folderImage;
  }

  public static Image getCameraImage(Widget widget) {
    if (cameraImage == null) {
      synchronized (obj) {
        if (cameraImage == null) {
          cameraImage = getImage(widget, "icons/obj16/camera.png");
        }
      }
    }
    return cameraImage;
  }

  public static Image getDesignImage(Widget widget) {
    if (designImage == null) {
      synchronized (obj) {
        if (designImage == null) {
          designImage = getImage(widget, "icons/obj16/design.png");
        }
      }
    }
    return designImage;
  }

}
