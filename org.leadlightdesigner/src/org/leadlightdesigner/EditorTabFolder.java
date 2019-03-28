package org.leadlightdesigner;

import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer2;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class EditorTabFolder extends CTabFolder {

//  private final Map<String, Image> imageMap = new HashMap<>();
  
  
  public EditorTabFolder(Composite parent) {
    super(parent, SWT.BORDER);
    setRenderer(new CTabFolderRenderer2(this));
    
//    addDisposeListener(e -> {
//      for (Image image : imageMap.values()) {
//        image.dispose();
//      }
//    });
    addMouseListener(new MouseListener() {
      @Override
      public void mouseDoubleClick(MouseEvent arg0) {
      }

      @Override
      public void mouseDown(MouseEvent ev) {
        EditorTabFolder tabFolder = (EditorTabFolder)ev.getSource();
        System.out.println(".... mouse down " + ev);
        for (TabItem item : tabFolder.getIItems()) {
          if (item.getBounds().contains(ev.x, ev.y)) {
            System.out.println("mouse down over " + item.getText());
            
            Menu menu = new Menu (tabFolder.getShell(), SWT.POP_UP);
            item.buildPopupMenu(menu);
            tabFolder.setMenu(menu);
            break;
          }
        }
      }

      @Override
      public void mouseUp(MouseEvent arg0) {
      }
    });
    
  }

  
  public TabItem[] getIItems() {
    CTabItem[] items = super.getItems();
    TabItem[] items2 = new TabItem[items.length];
    for (int i = 0; i < items.length; i++) {
      items2[i] = (TabItem)items[i];
    }
    return items2;
  }
  
  
  public boolean contains (Path path) {
    String pathx = path.toString();
    
    CTabItem[] items = super.getItems();
    for (CTabItem item : items) {
      String ttt = item.getToolTipText();
      if (pathx.equals(ttt)) {
        return true;
      }
    }
    return false;
  }
  
  
  public TabItem getISelection() {
    return (TabItem)super.getSelection();
  }
  
  
  public TabItem getIItem (int index) {
    return (TabItem)super.getItem(index);
  }
  
  
//  public Image getImage(String key, String fileName) {
//    Image image = imageMap.get(key);
//    if (image == null) {
//      image = new Image(getDisplay(), fileName);
//      imageMap.put(key, image);
//    }
//    return image;
//  }
  
  
  @Override
  protected void checkSubclass() {
  }

  
}
