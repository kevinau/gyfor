package org.leadlightdesigner.design;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.leadlightdesigner.IManagedFileType;
import org.leadlightdesigner.ImageRegistry;

public class DesignManagedFileType implements IManagedFileType {

  @Override
  public Image getFileIcon(Widget widget) {
    return ImageRegistry.getDesignImage(widget);
  }

  @Override
  public String getDescription() {
    return "design";
  }

  
  @Override
  public String getFilterExtensions() {
    return "*.design";
  }
  

  @Override
  public String getFilterNames() {
    return "design files";
  }
  
}
