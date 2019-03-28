package org.leadlightdesigner.files;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.swt.widgets.Menu;
import org.leadlightdesigner.EditorTabFolder;

public class NavigatorFile extends NavigatorItem {

  public NavigatorFile (NavigatorModel model, Path rootDir, Path relativePath) {
    super (model, rootDir, relativePath);
  }

  
  @Override
  public String getImageName () {
    return null;
  }

  
  @Override
  public void dump (int level) {
    indent (level);
    System.out.println("Navigator file: " + getFile());
  }
  
  
  @Override
  public void buildContextMenu (Menu menu, EditorTabFolder tabFolder) {
    buildContextDelete (menu, true);
  }

  
}
