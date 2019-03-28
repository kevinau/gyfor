package org.leadlightdesigner.files;

import java.io.File;
import java.nio.file.Path;

public class NavigatorLinkedItem extends NavigatorItem {

  private final NavigatorItem target;
  
  public NavigatorLinkedItem (NavigatorItem target) {
    super (target.getModel(), target.getRootDir(), target.getRelativePath());
    if (target instanceof NavigatorLinkedItem) {
      throw new RuntimeException("Linked item cannot link a linked item");
    }
    this.target = target;
  }

  
  @Override
  public String getImageName() {
    return target.getImageName();
  }

  
  @Override
  public File getFile () {
    return target.getFile();
  }
  
  
  @Override
  public String getName () {
    return "\u2192 " + target.getName();
  }
  
  
  @Override
  public boolean hasChildren () {
    return false;
  }
  
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("Linked item:");
    target.dump(level + 1);
  }

}
