package org.leadlightdesigner.files;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.leadlightdesigner.CachedImage;
import org.leadlightdesigner.EditorTabFolder;
import org.leadlightdesigner.ImageRegistry;
import org.leadlightdesigner.TabItem;

public class NavigatorTabItem extends TabItem {

  private Tree tree;
  
  // private static ITabClass<NavigatorTabItem> navigatorTabClass = new
  // NavigatorTabClass();

  private void buildTreeItem(TreeItem treeItem, NavigatorItem navItem) {
//    String name = item.getName();
//    if (linkedItem) {
//      name = "\u2192 " + name;
//    }
    treeItem.setText(navItem.getName());
    String imageName = navItem.getImageName();
    if (imageName != null) {
      treeItem.setImage(CachedImage.getImage(treeItem, imageName));
    }
    treeItem.setData(navItem);
    
    if (navItem.hasChildren()) {
      buildSubtree(treeItem, navItem.getChildren());
    }
  }
  

  private void buildSubtree(TreeItem parent, List<NavigatorItem> items) {
    for (NavigatorItem item : items) {
      TreeItem treeItem = new TreeItem(parent, 0);
      buildTreeItem(treeItem, item);
    }
  }

  private void buildTree(Composite composite, EditorTabFolder tabFolder, NavigatorModel model) {
    tree = new Tree(composite, SWT.NONE);

    for (NavigatorItem item : model.getItems()) {
      TreeItem treeItem = new TreeItem(tree, 0);
      buildTreeItem(treeItem, item);
    }

    final Menu menu = new Menu(tree);
    tree.setMenu(menu);
    menu.addMenuListener(new MenuAdapter() {
      @Override
      public void menuShown(MenuEvent ev) {
        MenuItem[] items = menu.getItems();
        for (int i = 0; i < items.length; i++) {
          items[i].dispose();
        }
        TreeItem selectedItem = tree.getSelection()[0];
        NavigatorItem navItem = (NavigatorItem)selectedItem.getData();
        navItem.buildContextMenu(menu, tabFolder);
//        MenuItem newItem = new MenuItem(menu, SWT.NONE);
//        newItem.setText("Menu for " + tree.getSelection()[0].getText());
      }
    });

  }

  
  private TreeItem findTreeItem (TreeItem treeItem, NavigatorItem navItem) {
    if (treeItem.getData().equals(navItem)) {
      return treeItem;
    }
    for (TreeItem childTreeItem : treeItem.getItems()) {
      TreeItem ti = findTreeItem(childTreeItem, navItem);
      if (ti != null) {
        return ti;
      }
    }
    return null;
  }
  
  
  private TreeItem findTreeItem (NavigatorItem item) {
    return findTreeItem (tree.getTopItem(), item);
  }

  
  public NavigatorTabItem(EditorTabFolder parent, Path rootDir) {
    super(parent, ImageRegistry.getFolderImage(parent));

    super.setText(rootDir.getFileName().toString());
    //super.setToolTipText(rootDir.toFile().getAbsolutePath());

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new FillLayout());
    super.setControl(composite);
    
    NavigatorModel model = new NavigatorModel(rootDir);
    model.addChangeEvent(new INavigatorEvent() {
      @Override
      public void itemAdded(NavigatorItem parentItem, NavigatorItem item) {
        TreeItem parentTreeItem = findTreeItem(parentItem);
        TreeItem newTreeItem = new TreeItem(parentTreeItem, SWT.NONE);
        buildTreeItem(newTreeItem, item);
//        model.dump();
//        if (!tree.isDisposed()) {
//          tree.dispose();
//        }
//        System.out.println("============================");
//        NavigatorTabItem.this.buildTree(composite, parent, model);
//        composite.layout(true, true);
      }
      @Override
      public void itemRemoved(NavigatorItem parentItem, NavigatorItem item) {
        
      }
    });
    buildTree(composite, parent, model);
  }

//  @Override
//  public boolean canSave() {
//    return false;
//  }

  @Override
  public boolean isCloseable() {
    return false;
  }


  @Override
  public boolean close(String action) {
    throw new IllegalStateException("This item should not be closed");
  }


//  @Override
//  public ITabClass<?> getTabClass() {
//    return navigatorTabClass;
//  }

}
