package org.leadlightdesigner;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;

public interface ITabItem {
  
  public ITabClass<?> getTabClass();

  public boolean isModified();
  
  public boolean canSave();
  
  public boolean close (String action);
  
  public void dispose();
  
  public default boolean save() {
    return false;
  }
  
  
  public default boolean saveAs() {
    return false;
  }

  public void buildPopupMenu(Menu menu);

  public Rectangle getBounds();

  public String getText();
  
  
//  public void addChangeListener(TabItemChangeListener x);
//  
//  public void removeChangeListener(TabItemChangeListener x);
  
}
