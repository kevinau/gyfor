package org.leadlightdesigner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


public abstract class FileTabItem extends TabItem {

  private final IManagedFileType managedFileType;

  private Path rootDir;
  
  private Path managedFilePath;
  
  private boolean modified = false;

  
  public FileTabItem(CTabFolder parent, Path rootDir, Path managedFilePath, IManagedFileType managedFileType) {
    super(parent, managedFileType.getFileIcon(parent));
    this.managedFileType = managedFileType;
    this.rootDir = rootDir;  
    this.managedFilePath = managedFilePath;
        
    super.setText(managedFilePath.getFileName().toString());
    super.setToolTipText(managedFilePath.toString());
  }
  
  
  protected Path getRootDir () {
    return rootDir;
  }
  
  
  protected Path getManagedFlePath () {
    return managedFilePath;
  }
  
  
  @Override
  public void setText(String tabName) {
    if (modified) {
      super.setText("*" + tabName);
    } else {
      super.setText(tabName);
    }
  }


  public void setModified(boolean modified) {
    if (this.modified != modified) {
      String name = getText();
      if (modified) {
        if (name.startsWith("*")) {
          throw new IllegalStateException();
        }
        super.setText("*" + name);
      } else {
        if (!name.startsWith("*")) {
          throw new IllegalStateException();
        }
        super.setText(name.substring(1));
      }
      this.modified = modified;
    }
  }

  
  public boolean isModified() {
    return modified;
  }
  
  

  @Override
  public void buildPopupMenu (final Menu menu) {
    MenuItem item = new MenuItem (menu, SWT.PUSH);
    item.setText ("Close");
    item.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent ev) {
        if (isCloseable()) {
          boolean ok = close("close");
          if (ok) {
            FileTabItem.this.dispose();
          }
        }
      }
    });
  }

  
  @Override
  public boolean isCloseable () {
    return true;
  }
  
  
  @Override
  public boolean close(String action) {
    if (isModified()) {
      String description = managedFileType.getDescription();
      // Ask the user if they want to save current design
      ConfirmNoSaveDialog saveDialog = new ConfirmNoSaveDialog(getParent().getShell(), description, managedFilePath.getFileName().toString(), action);
      int result = saveDialog.show();
      if(result == SWT.CANCEL) {
        return false;
      } else if(result == SWT.YES) {
        if (!save()) return false;
      }
    }
    return true;
  }

  
  public boolean save() {
    if (!Files.exists(managedFilePath)) {
      return saveAs();
    }
    return saveManagedFile(managedFilePath);
  }
    
  
  public boolean saveAs() {
    FileDialog saveDialog = new FileDialog(getParent().getShell(), SWT.SAVE);
    saveDialog.setText("Save as " + managedFileType.getDescription());
    saveDialog.setOverwrite(true);
    
    saveDialog.setFilterExtensions(new String[] {
        managedFileType.getFilterExtensions(),
        "*.*",
    });
    saveDialog.setFilterNames(new String[] {
        managedFileType.getFilterNames(), 
        "All Files ",
    });
    
    Path filterPath = getFilterPath(rootDir);
    saveDialog.setFilterPath(filterPath.toString());
    String saveFileName = saveDialog.open();
    if (saveFileName == null) {
      return false;
    }
    
    Path savePath = Paths.get(saveFileName);
    boolean saved = saveManagedFile(savePath);
    if (saved) {
      setModified(false);
    }
    return saved;
  }

  
  protected Path getFilterPath (Path rootDir) {
    return rootDir;
  }
  
  
  protected abstract boolean saveManagedFile (Path savePath);
 
}
