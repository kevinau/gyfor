package org.leadlightdesigner.guide;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.leadlightdesigner.CachedImage;
import org.leadlightdesigner.IManagedFileType;

public class GuideManagedFileType implements IManagedFileType {

  @Override
  public Image getFileIcon(Widget widget) {
    return CachedImage.getImage(widget, "icons/obj16/guide.png");
  }

  @Override
  public String getDescription() {
    return "guide image";
  }

  
  @Override
  public String getFilterExtensions() {
    return "*.png";
  }
  

  @Override
  public String getFilterNames() {
    return "png image files";
  }
  
}
