package org.leadlightdesigner.files;

import java.nio.file.Path;

import org.eclipse.swt.widgets.Menu;
import org.leadlightdesigner.EditorTabFolder;
import org.leadlightdesigner.guide.GuideAttributes;

public class NavigatorGuide extends NavigatorSourcedItem {

  private final GuideAttributes attrib;
  
  public NavigatorGuide (NavigatorModel model, Path rootDir, Path relativePath, Path sourcePath) {
    super (model, rootDir, relativePath);
    this.attrib = new GuideAttributes(sourcePath);
  }

  
  public NavigatorGuide (NavigatorModel model, Path rootDir, Path relativePath, GuideAttributes attrib) {
    super (model, rootDir, relativePath);
    this.attrib = attrib;
  }

  
  public GuideAttributes getAttributes() {
    return attrib;
  }
  
  
  @Override
  public String getImageName () {
    return "icons/obj16/guide.png";
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
    System.out.println("Navigator guide: " + getFile() + ",  " + attrib);
  }
  

}
