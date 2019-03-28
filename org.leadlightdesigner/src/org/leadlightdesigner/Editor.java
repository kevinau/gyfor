package org.leadlightdesigner;

import static org.eclipse.swt.events.MenuListener.menuShownAdapter;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.leadlightdesigner.design.DesignTabItem;
import org.leadlightdesigner.files.NavigatorGuide;
import org.leadlightdesigner.files.NavigatorImage;
import org.leadlightdesigner.files.NavigatorTabItem;

public class Editor {

  private static ResourceBundle resAddressBook = ResourceBundle.getBundle("leadlight-designer");

  private Shell shell;
  
  private MenuItem saveMenuItem;
  private MenuItem saveAsMenuItem;
//  private MenuItem closeMenuItem;
  
  private EditorTabFolder tabFolder;

 
  public static void main(String[] args) {
    Display display = new Display();
    Editor application = new Editor();
    
    Path rootDir = Paths.get("workspace");
    System.out.println(rootDir.toFile().getAbsolutePath());
    
    Shell shell = application.open(display, rootDir);
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
  
  
  public Shell open(Display display, Path rootDir) {
    shell = new Shell(display);
    shell.setText(resAddressBook.getString("Title_bar"));
    shell.setLayout(new FillLayout());
    shell.addShellListener(ShellListener.shellClosedAdapter(e -> e.doit = prepareExit("exit")));

    createMenuBar(rootDir);
    tabFolder = new EditorTabFolder(shell);
    tabFolder.setSimple(true);
    
    // Create navigator tab
    NavigatorTabItem navTabItem = new NavigatorTabItem(tabFolder, rootDir);
//    ITabClass<NavigatorTabItem> tabClass = new NavigatorTabClass();
//    NavigatorTabItem tabItem = tabClass.newInstance(tabFolder);
    //tabItem.setRootDir(rootDir);

    shell.open();
    return shell;
  }

  
  /**
   * Creates the menu at the top of the shell where most
   * of the programs functionality is accessed.
   *
   * @return    The <code>Menu</code> widget that was created
   */
  private Menu createMenuBar(Path rootDir) {
    Menu menuBar = new Menu(shell, SWT.BAR);
    shell.setMenuBar(menuBar);

    //create each header and subMenu for the menuBar
    createFileMenu(menuBar, rootDir);
    //createEditMenu(menuBar);
    //createSearchMenu(menuBar);
    createHelpMenu(menuBar);

    return menuBar;
  }

  
  private void createFileMenu(Menu menuBar, Path rootDir) {
    //File menu.
    MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
    item.setText(resAddressBook.getString("File_menu_title"));
    Menu menu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(menu);
    /**
     * Adds a listener to handle enabling and disabling
     * some items in the File submenu.
     */
    menu.addMenuListener(menuShownAdapter(e -> {
//      TabItem tabItem = tabFolder.getISelection();

//      closeMenuItem.setEnabled(tabItem != null);
//      saveMenuItem.setEnabled(tabItem != null && tabItem.canSave() && tabItem.isModified());
//      saveAsMenuItem.setEnabled(tabItem != null && tabItem.canSave());   
    }));


    //new MenuItem(menu, SWT.SEPARATOR);

    //File -> 'New' items
    MenuItem newDesignMenuItem = new MenuItem(menu, SWT.CASCADE);
    newDesignMenuItem.setText("&New design");
    newDesignMenuItem.setImage(ImageRegistry.getDesignImage(menu));
    newDesignMenuItem.addSelectionListener(widgetSelectedAdapter( ev -> {
      DesignTabItem designTab = new DesignTabItem(tabFolder, rootDir);
      tabFolder.setSelection(designTab);
    }));
    
    MenuItem newFinalMenuItem = new MenuItem(menu, SWT.CASCADE);
    newFinalMenuItem.setText("&New final image");
    newFinalMenuItem.addSelectionListener(widgetSelectedAdapter( ev -> {
      //DesignTabItem designTab = new DesignTabItem(tabFolder, rootDir);
      //ztabFolder.setSelection(designTab);
    }));
    
//    MenuItem newGuideMenuItem = new MenuItem (newSubmenu, SWT.PUSH);
//    newGuideMenuItem.setText ("&Guide image");
//    newGuideMenuItem.addSelectionListener(widgetSelectedAdapter( e -> {
//      ITabClass<GuideTabItem> tabClass = new GuideTabClass();
//      ITabItem tabItem = tabClass.newInstance(tabFolder);
//    }));

//    MenuItem newDesign = new MenuItem (newSubmenu, SWT.PUSH);
//    newDesign.setText ("&Design");
//    MenuItem newFinalImage = new MenuItem (newSubmenu, SWT.PUSH);
//    newFinalImage.setText ("&Final image");

    //File -> Open
//    MenuItem subItem = new MenuItem(menu, SWT.NONE);
//    subItem.setText(resAddressBook.getString("Open_design"));
//    subItem.setAccelerator(SWT.MOD1 + 'O');
//    subItem.addSelectionListener(widgetSelectedAdapter( e -> {
//      loadDesign();
//    }));

    new MenuItem(menu, SWT.SEPARATOR);

    //File -> Close.
//    closeMenuItem = new MenuItem(menu, SWT.NONE);
//    closeMenuItem.setText(resAddressBook.getString("Close_design"));
//    closeMenuItem.setAccelerator(SWT.MOD1 + 'C');
//    closeMenuItem.addSelectionListener(widgetSelectedAdapter( e -> {
//      closeDesign(tabFolder.getISelection(), "close");
//    }));

//    //File -> Save.
//    saveMenuItem = new MenuItem(menu, SWT.NONE);
//    saveMenuItem.setText(resAddressBook.getString("Save_design"));
//    saveMenuItem.setAccelerator(SWT.MOD1 + 'S');
//    saveMenuItem.addSelectionListener(widgetSelectedAdapter(e -> {
//      ITabItem selectedTab = tabFolder.getISelection();
//      if (selectedTab != null) {
//        saveEditedFile(selectedTab);
//      }
//    }));

//    //File -> Save As.
//    saveAsMenuItem = new MenuItem(menu, SWT.NONE);
//    saveAsMenuItem.setText(resAddressBook.getString("Save_design_as"));
//    saveAsMenuItem.setAccelerator(SWT.MOD1 + SWT.SHIFT + 'S');
//    saveAsMenuItem.addSelectionListener(widgetSelectedAdapter( e -> {
//      ITabItem selectedTab = tabFolder.getISelection();
//      if (selectedTab != null) {
//        saveEditedFileAs(selectedTab);
//      }
//    }));

//    new MenuItem(menu, SWT.SEPARATOR);

    //File -> Exit.
    MenuItem subItem = new MenuItem(menu, SWT.NONE);
    subItem.setText(resAddressBook.getString("Exit"));
    subItem.addSelectionListener(widgetSelectedAdapter( e -> {
      boolean doit = prepareExit("exit");
      if (doit) {
        shell.dispose();
      }
    }));
  }

    
//  private void createTabItem (DesignObject obj, File file) {
//    FileTabItem tabItem = new FileTabItem(tabFolder, obj);
//    tabItem.setText(obj.getName());
//    tabItem.setModified(false);
//    tabItem.setFile(file);
//    
//    Canvas canvas = new DesignCanvas(tabFolder, obj);
//    tabItem.setControl(canvas);
//    
//    tabFolder.setSelection(tabItem);
//  }

  
  public boolean closeDesign(TabItem tabItem, String action) {
    if (!tabItem.close(action)) {
      return false;
    }
    tabItem.dispose();
    return true;
  }
  
  
//  public boolean saveEditedFile(TabItem tabItem) {
//    return tabItem.save();
//  }
//  
//  
//  public boolean saveEditedFileAs(TabItem tabItem) {
//    return tabItem.saveAs();
//  }
  
  
//  private void loadDesign() {
//    FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
//
//    fileDialog.setFilterExtensions(new String[] {"*.json;", "*.*"});
//    fileDialog.setFilterNames(new String[] {"Design files (*.json)", "All Files "});
//    String name = fileDialog.open();
//    if(name == null) return;
//
//    File file = new File(name);
//    if (!file.exists()) {
//      displayError("File " + file.getName() + " does not exist");
//      return;
//    }
//
//    System.out.println("Loading " + file.getAbsolutePath());
//    DesignObject obj = FileTabItem.loadManagedObject(file, DesignObject.class);
//    obj.dump();
//    createTabItem(obj, file);
//  }

  

  private void displayError (String msg) {
    MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
    box.setMessage(msg);
    box.open();
  }
  
  

  /**
   * Creates all the items located in the Help submenu and
   * associate all the menu items with their appropriate
   * functions.
   *
   * @param menuBar Menu
   *        the <code>Menu</code> that file contain
   *        the Help submenu.
   */
  private void createHelpMenu(Menu menuBar) {

    //Help Menu
    MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
    item.setText(resAddressBook.getString("Help_menu_title"));
    Menu menu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(menu);

    //Help -> About Text Editor
    MenuItem subItem = new MenuItem(menu, SWT.NONE);
    subItem.setText(resAddressBook.getString("About"));
    subItem.addSelectionListener(widgetSelectedAdapter( e -> {
      MessageBox box = new MessageBox(shell, SWT.NONE);
      box.setText(resAddressBook.getString("About_1") + shell.getText());
      box.setMessage(shell.getText() + resAddressBook.getString("About_2"));
      box.open();
    }));
  }


  /**
   * Do what is necessary to exit the editor. Return true is the preparation was
   * successful (the exit can proceed) or false otherwise (no exit should take
   * place).
   */
  private boolean prepareExit(String action) {
    int i = 0;
    while (i < tabFolder.getItemCount()) {
      TabItem tabItem = tabFolder.getIItem(i);
      if (tabItem.isCloseable()) {
        boolean ok = closeDesign(tabItem, action);
        if (!ok) {
          return false;
        }
      } else {
        i++;
      }
    }
    return true;
  }

}
