package org.leadlightdesigner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.gyfor.nio.SafeWriter;
import org.leadlightdesigner.design.DesignObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EditFileTabItem extends TabItem {

  private static Logger logger = LoggerFactory.getLogger(EditFileTabItem.class);
  
  private final IManagedFileType managedFileType;
  
  private boolean modified;

  private File managedFile;
  
  private Object fileObject;
  
  
  public EditFileTabItem(EditorTabFolder parent, IManagedFileType managedFileType, int style) {
    super(parent, managedFileType.getFileIcon(parent), style);
    this.managedFileType = managedFileType;
  }

  
  public void setFile(File file) {
    this.file = file;
    if (file == null) {
      setToolTipText(null);
    } else {
      setToolTipText(file.getAbsolutePath());
    }
  }

  
  public void setText(String designName) {
    if (modified) {
      super.setText("*" + designName);
    } else {
      super.setText(designName);
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
  
  
  protected void buildCloseMenuItem (Menu menu) {
    MenuItem item = new MenuItem (menu, SWT.PUSH);
    item.setText ("Save");
    item.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetDefaultSelected(SelectionEvent ev) {
        save();
      }

      @Override
      public void widgetSelected(SelectionEvent ev) {
        widgetDefaultSelected(ev);
      }
    });

    MenuItem item2 = new MenuItem (menu, SWT.PUSH);
    item2.setText ("Save as...");
    item2.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetDefaultSelected(SelectionEvent ev) {
        saveAs();
      }

      @Override
      public void widgetSelected(SelectionEvent ev) {
        widgetDefaultSelected(ev);
      }
    });

    MenuItem item3 = new MenuItem (menu, SWT.PUSH);
    item3.setText ("Close");
    item3.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetDefaultSelected(SelectionEvent ev) {
        close("close");
      }

      @Override
      public void widgetSelected(SelectionEvent ev) {
        widgetDefaultSelected(ev);
      }
    });    
  }

  
  public boolean close(String action) {
    if (isModified()) {
      DesignObject obj = getManagedObject();
      // Ask the user if they want to save current design
      ConfirmNoSaveDialog saveDialog = new ConfirmNoSaveDialog(getParent().getShell(), obj.getName(), action);
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
    if (managedFile == null) {
      return saveAs();
    }
    return saveManagedObject(managedFile);
  }
    
  
  public boolean saveAs() {
    logger.info("Save as");
    FileDialog saveDialog = new FileDialog(getParent().getShell(), SWT.SAVE);
    saveDialog.setText("Save as " + managedFileType.getManagedFileName() + " file");
    saveDialog.setOverwrite(true);
    
    saveDialog.setFilterExtensions(new String[] {
        managedFileType.getFilterExtensions(),
        "*.*",
    });
    saveDialog.setFilterNames(new String[] {
        managedFileType.getFilterNames(), 
        "All Files ",
    });

    saveDialog.open();
    String name = saveDialog.getFileName();
    logger.info("Save as: file name {}", name);

    if(name.isEmpty()) return false;
    
    String defaultExtension = managedFileType.getDefaultExtension();
    if(defaultExtension != null && !name.endsWith(defaultExtension)) {
      name += defaultExtension;
    }
    
    File file = new File(saveDialog.getFilterPath(), name);

    // Set managed object name (before saving)
    Object namedObject = getManagedObject();
    if (namedObject instanceof INamedObject) {
      String n = name.substring(0, name.length() - 5);
      n = Character.toUpperCase(n.charAt(0)) + n.substring(1);
      ((INamedObject)namedObject).setName(n);
      setText(n);
    }
    
    return managedFileType.save(obj, file);
  }


//  static <T> T loadFile file, Class<T> klass) {
//    logger.info("Load managed object {}", file);
//    //Gson gson = new GsonBuilder().create();
//
//    try (Reader reader = new FileReader(file)) {
//      T obj = DesignObject.gson.fromJson(reader, klass);
//      logger.info("Reading done");
//      return obj;
//    } catch (Exception ex) {
//      logger.info("Exception when loading file", ex);
//      return null;
//    }
//  }
//  
//  
//  private boolean saveManagedObject(File file) {
//    logger.info("Save managed object {}", file);
//    //Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//    try (SafeWriter writer = new SafeWriter(file.toPath())) {
//      String s = DesignObject.gson.toJson((Object)getManagedObject());
//      writer.write(s);
//      writer.commit();
//      logger.info("Saving done");
//      return true;
//    } catch (Exception ex) {
//      logger.info("Exception while saving file", ex);
//      return false;
//    }
//  }

}
