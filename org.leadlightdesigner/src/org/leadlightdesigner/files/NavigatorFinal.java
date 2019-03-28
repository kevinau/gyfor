package org.leadlightdesigner.files;

import java.nio.file.Path;

import org.eclipse.swt.widgets.Menu;
import org.leadlightdesigner.EditorTabFolder;

public class NavigatorFinal extends NavigatorSourcedItem {

  private final FinalAttributes attrib;
  
  public NavigatorFinal (NavigatorModel model, Path rootDir, Path relativePath, FinalAttributes attrib) {
    super (model, rootDir, relativePath);
    this.attrib = attrib;
  }

  
  public FinalAttributes getAttributes() {
    return attrib;
  }
  
  
  @Override
  public String getImageName () {
    return "icons/obj16/final.png";
  }


  @Override
  public Path getSourcePath() {
    return attrib.getSource();
  }
  
  
  @Override
  public void buildContextMenu (Menu menu, EditorTabFolder tabFolder) {
    buildContextDelete (menu, true);
  }

  
  @Override
  public void dump (int level) {
    indent (level);
    System.out.println("Navigator final: " + getFile() + ",  " + attrib);
  }
  
}
