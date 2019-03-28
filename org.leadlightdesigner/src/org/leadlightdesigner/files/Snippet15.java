package org.leadlightdesigner.files;

import java.io.File;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.leadlightdesigner.ImageRegistry;

public class Snippet15 {

  private static final String[] imageSuffixes = ImageIO.getReaderFileSuffixes();
  
  private static boolean isImageFile (File file) {
    String fileName = file.getName().toLowerCase();
    for (String suffix : imageSuffixes) {
      if (fileName.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }
  
  
  private void buildTreeItem (File file, TreeItem item) {
    item.setText(file.getName());
    if (file.isDirectory()) {
      item.setImage(ImageRegistry.getFolderImage(item));
      buildSubtree (file, item);
    } else {
      if (isImageFile(file)) {
        item.setImage(ImageRegistry.getCameraImage(item));
      }
    }
  }
  
  
  private void buildSubtree (File dir, TreeItem parent) {
    File[] files = dir.listFiles();
    for (File file : files) {
      TreeItem iItem = new TreeItem(parent, 0);
      buildTreeItem (file, iItem);
    }
  }
  
  
  private void buildTree (File rootDir, Shell shell) {
    final Tree parent = new Tree(shell, SWT.BORDER);

    File[] files = rootDir.listFiles();
    for (File file : files) {
      TreeItem iItem = new TreeItem(parent, 0);
      buildTreeItem (file, iItem);
    }
  }
  
  
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    
    Snippet15 snippet = new Snippet15();
    File rootDir = new File("C:/Users/Kevin/Accounts/JH Shares");
    snippet.buildTree(rootDir, shell);
   
    shell.setSize(200, 200);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}
