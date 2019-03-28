package org.leadlightdesigner;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

public interface IManagedFileType {

  public Image getFileIcon(Widget widget);
  
  public String getDescription();
  
  public String getFilterExtensions();
  
  public String getFilterNames();
  
  public default String getDefaultExtension() {
    return null;
  };
  
}
