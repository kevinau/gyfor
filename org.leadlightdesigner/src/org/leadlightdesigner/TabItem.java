package org.leadlightdesigner;

import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public abstract class TabItem extends CTabItem {

  public TabItem(CTabFolder parent, Image tabImage) {
    super(parent, SWT.NONE);
    setImage(tabImage);
  }
  

  protected void buildCloseMenuItem (Menu menu) {
    MenuItem item = new MenuItem (menu, SWT.PUSH);
    item.setText ("Close");
    item.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetDefaultSelected(SelectionEvent ev) {
        TabItem.this.dispose();
      }

      @Override
      public void widgetSelected(SelectionEvent ev) {
        widgetDefaultSelected(ev);
      }
      
    });
  }

  
  public void buildPopupMenu (final Menu menu) {
  }

  
  public void setNames (String shortName, String longName) {
    setText(shortName);
    super.setToolTipText(longName);
  }
  
  
  public void setNames (Path path) {
    setNames(path.getFileName().toString(), path.toString());
  }

  
  @Override
  public void setText (String shortName) {
    super.setText(shortName);
  }
  
  
  public String getShortName() {
    return super.getText();
  }
  
  
  public String getLongName() {
    return super.getToolTipText();
  }

  
  public abstract boolean isCloseable();
  

  public boolean close(String action) {
    return true;
  }

}
