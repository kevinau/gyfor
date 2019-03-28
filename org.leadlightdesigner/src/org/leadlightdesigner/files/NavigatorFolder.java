package org.leadlightdesigner.files;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.widgets.Menu;
import org.leadlightdesigner.EditorTabFolder;

public class NavigatorFolder extends NavigatorItem {

  private final List<NavigatorItem> items;
  
  public NavigatorFolder (NavigatorModel model, Path rootDir, Path relativePath, List<NavigatorItem> items) {
    super (model, rootDir, relativePath);
    this.items = items;
  }

  
  @Override
  public boolean hasChildren() {
    return true;
  }
  
  
  @Override
  public List<NavigatorItem> getChildren () {
    return items;
  }
  
  
  @Override
  public String getImageName () {
    return "icons/obj16/fldr_obj.png";
  }

  
  @Override
  public void walkItems (Consumer<NavigatorItem> consumer) {
    for (NavigatorItem item : items) {
      item.walkItems(consumer);
    }
  }
  
  
  @Override
  public void buildContextMenu (Menu menu, EditorTabFolder tabFolder) {
    buildContextDelete (menu, items.isEmpty());
  }


  @Override
  public void dump (int level) {
    indent (level);
    System.out.println("Navigator folder: " + getFile());
    for (NavigatorItem item : items) {
      item.dump(level + 1);
    }
  }
  
}
