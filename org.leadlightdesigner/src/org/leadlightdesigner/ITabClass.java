package org.leadlightdesigner;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;

public interface ITabClass<T extends ITabItem> {

  public T newInstance(CTabFolder tabFolder);
  
  public Image getTabImage(Widget widget);

}
