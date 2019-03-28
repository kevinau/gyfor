package org.leadlightdesigner.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.leadlightdesigner.EditorTabFolder;
import org.leadlightdesigner.guide.GuideTabItem;

public class NavigatorImage extends NavigatorItem implements ISource {

  private final List<NavigatorItem> derived = new ArrayList<>();
  
  
  public NavigatorImage (NavigatorModel model, Path rootDir, Path relativePath) {
    super (model, rootDir, relativePath);
  }

  
  @Override
  public String getImageName () {
    return "icons/obj16/camera.png";
  }

  
  @Override
  public boolean hasChildren () {
    return !derived.isEmpty();
  }
  
  
  @Override
  public List<NavigatorItem> getChildren () {
    return derived;
  }
  
  
  private Rectangle guideSizeDialog (Shell shell) {
    final Rectangle[] size = new Rectangle[1];
    
    final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    dialog.setText("Size of target");
    
    Label label = new Label (dialog, SWT.NONE);
    label.setText ("x");
    Label label2 = new Label (dialog, SWT.NONE);
    label2.setText ("mm");
 
    Text xText = new Text(dialog, SWT.BORDER);

    Text yText = new Text(dialog, SWT.BORDER);

    Button okButton = new Button (dialog, SWT.PUSH);
    okButton.setText ("&OK");
    okButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent arg0) {
        int width = Integer.parseInt(xText.getText());
        int height = Integer.parseInt(yText.getText());
        size[0] = new Rectangle(0, 0, width, height);
        dialog.close();
      }
    });
    
    Button cancelButton = new Button (dialog, SWT.PUSH);
    cancelButton.setText ("&Cancel");
    cancelButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent arg0) {
        size[0] = null;
        dialog.close();
      }
    });

    FormLayout form = new FormLayout ();
    form.marginWidth = form.marginHeight = 8;
    dialog.setLayout (form);
    
    FormData labelData = new FormData ();
    labelData.left = new FormAttachment (xText, 8);
    labelData.top = new FormAttachment (xText, 0, SWT.TOP);
    label.setLayoutData (labelData);
    
    FormData yData = new FormData ();
    yData.left = new FormAttachment (label, 8);
    yData.top = new FormAttachment (label, 0, SWT.TOP);
    yText.setLayoutData (yData);
    
    FormData label2Data = new FormData ();
    label2Data.left = new FormAttachment (yText, 8);
    label2Data.top = new FormAttachment (yText, 0, SWT.TOP);
    label2.setLayoutData (label2Data);
    
    FormData cancelData = new FormData ();
    cancelData.right = new FormAttachment (label2, 0, SWT.RIGHT);
    cancelData.top = new FormAttachment (yText, 8, SWT.BOTTOM);
    cancelButton.setLayoutData (cancelData);

    FormData okData = new FormData ();
    okData.right = new FormAttachment (cancelButton, -8, SWT.LEFT);
    okData.top = new FormAttachment (cancelButton, 0, SWT.TOP);
    okButton.setLayoutData (okData);
    
    dialog.setDefaultButton (okButton);
    dialog.pack ();
    
    /** get the size of the window */
    Rectangle bounds = shell.getBounds();
    
    /** get the size of the dialog */
    Rectangle rect = dialog.getBounds();

    /** calculate the centre */
    int x = bounds.x + (bounds.width - rect.width) / 2;
    int y = bounds.y + (bounds.height - rect.height) / 2;

    /** set the new location */
    dialog.setLocation(x, y);
    
    dialog.open ();
    Display display = shell.getDisplay();
    while (!dialog.isDisposed()) {
      if (!display.readAndDispatch()) {
          display.sleep();
      }
    }
    
    return size[0];
  }
  
  
  private Path buildGuidePath (Path relativeImagePath, EditorTabFolder tabFolder) {
    Path guideDir = relativeImagePath.resolveSibling("guide");
    
    String nameBase = "unnamed";
    int index = 0;
    
    Path guidePath;
    do {
      String name = nameBase;
      index++;
      if (index > 1) {
        name += "(" + index + ")";
      }
      name += ".png";
      guidePath = guideDir.resolve(name);
    } while (tabFolder.contains(guidePath) || Files.exists(guidePath));
    
    return guidePath;
  }
  
  
  @Override
  public void buildContextMenu (Menu menu, EditorTabFolder tabFolder) {
    MenuItem deleteItem = new MenuItem(menu, SWT.NONE);
    deleteItem.setText("New guide");
    deleteItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent ev) {
        Path imagePath = getRelativePath();

        Path guidePath = buildGuidePath(getRelativePath(), tabFolder);
        NavigatorGuide newNavigatorGuide = new NavigatorGuide(getModel(), getRootDir(), guidePath, imagePath);
        //getModel().addFile(guidePath, newNavigatorGuide);
        
        NavigatorImage.this.addDerived(newNavigatorGuide);
        GuideTabItem guideTab = new GuideTabItem(tabFolder, getRootDir(), imagePath, guidePath);
        tabFolder.setSelection(guideTab);
        //}
      }
    });

    buildContextDelete (menu, derived.isEmpty());
  }


  @Override
  public void dump (int level) {
    indent (level);
    System.out.println("Navigator image: " + getFile());
    for (NavigatorItem item : derived) {
      item.dump(level + 1);
    }
  }


  @Override
  public void addDerived(NavigatorItem item) {
    NavigatorLinkedItem linkedItem = new NavigatorLinkedItem(item);
    derived.add(linkedItem);
    getModel().fireItemAddedEvents(NavigatorImage.this, linkedItem);
  }
    
  
  @Override
  public String toString () {
    return "NavigatorImage[" + super.toString() + "]";
  }
  
}
