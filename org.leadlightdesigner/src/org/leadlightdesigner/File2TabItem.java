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

public class File2TabItem extends TabItem {

  private static Logger logger = LoggerFactory.getLogger(File2TabItem.class);
  
  private final IManagedFileType managedFileType;
  
  private File managedFile;
  
  private Object fileObject;
  
  
  public File2TabItem(EditorTabFolder parent, IManagedFileType managedFileType) {
    super(parent, managedFileType.getFileIcon(parent));
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

  
  public boolean close(String action) {
    return true;
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
