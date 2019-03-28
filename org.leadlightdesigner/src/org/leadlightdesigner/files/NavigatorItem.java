package org.leadlightdesigner.files;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.leadlightdesigner.EditorTabFolder;

public abstract class NavigatorItem {

  private final NavigatorModel model;
  private final Path rootDir;
  private final Path relativePath;
  
  public NavigatorItem (NavigatorModel model, Path rootDir, Path relativePath) {
    this.model = model;
    this.rootDir = rootDir;
    this.relativePath = relativePath;
    if (relativePath.toString().contains("workspace")) {
      throw new RuntimeException(rootDir + " !!! " + relativePath);
    }
  }
    
  public abstract String getImageName();

  
  public NavigatorModel getModel () {
    return model;
  }
  
  
  public Path getRootDir () {
    return rootDir;
  }
  
  
  public Path getRelativePath () {
    return relativePath;
  }
  
  
  public File getFile () {
    return rootDir.resolve(relativePath).toFile();
  }
  
  
  public String getName () {
    return relativePath.getFileName().toString();
  }
  
  
  public boolean hasChildren () {
    return false;
  }
  
  
  public List<NavigatorItem> getChildren() {
    return null;
  }
  
  
  public void walkItems (Consumer<NavigatorItem> consumer) {
    consumer.accept(this);
  }

  
  public void linkSource (NavigatorModel model) {
  }

  
  protected void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }
  
  
  public abstract void dump (int level);
  
  
  protected void buildContextDelete (Menu menu, boolean enabled) {
    MenuItem deleteItem = new MenuItem(menu, SWT.NONE);
    deleteItem.setText("Delete");
    if (!enabled) {
      deleteItem.setEnabled(false);
    }
    deleteItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent arg0) {
        System.out.println("************* delete " + this);
      }
    });
  }


  public void buildContextMenu (Menu menu, EditorTabFolder tabFolder) {
  }

  
  @Override
  public String toString() {
    return "NavigatorItem[" + rootDir + ": " + relativePath + "]";
  }
}
