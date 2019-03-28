package org.leadlightdesigner.files;

import java.io.File;
import java.nio.file.Path;

public abstract class NavigatorSourcedItem extends NavigatorItem {

  public NavigatorSourcedItem(NavigatorModel model, Path rootDir, Path relativePath) {
    super(model, rootDir, relativePath);
  }

  
  public abstract Path getSourcePath ();
  
  
  @Override
  public void linkSource (NavigatorModel model) {
    Path sourcePath = getSourcePath();
    model.walkItems(i -> {
       if (i.getRelativePath().equals(sourcePath) && i instanceof ISource) {
        // Add this item to the source item
        ((ISource)i).addDerived(this);
      }
    });
  }
    
}
