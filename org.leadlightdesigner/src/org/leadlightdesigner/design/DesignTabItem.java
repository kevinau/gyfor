package org.leadlightdesigner.design;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Shell;
import org.leadlightdesigner.FileTabItem;

public class DesignTabItem extends FileTabItem {

  private final Shell shell;

  private double zoom = 1.0;
  
  
  public DesignTabItem(CTabFolder parent, Path rootDir) {
    super(parent, rootDir, Paths.get("unnamed.design"), new DesignManagedFileType());
    
    shell = parent.getShell();

//    super.setText(guidePath.getFileName().toString());
//    super.setToolTipText(guidePath.toString());

//    Image image = new Image (parent.getDisplay(), rootDir.resolve(sourcePath).toString());
//    double proportions = (double)(image.getBounds().height) / (double)(image.getBounds().width); 

    ScrolledComposite scomposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    DesignCanvas canvas = new DesignCanvas(scomposite);
    
    scomposite.setContent(canvas);

    //composite.setLayout(new FillLayout());
    //Label label = new Label (composite, SWT.NONE);
    //label.setImage (image);
    super.setControl(scomposite);
  }


  @Override
  protected boolean saveManagedFile(Path savePath) {
    // TODO Auto-generated method stub
    return false;
  }
  
  
}
