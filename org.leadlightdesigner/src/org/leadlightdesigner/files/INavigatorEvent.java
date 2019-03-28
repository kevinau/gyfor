package org.leadlightdesigner.files;

public interface INavigatorEvent {

  public void itemAdded (NavigatorItem parentItem, NavigatorItem item);

  public void itemRemoved (NavigatorItem pareentItem, NavigatorItem item);
  
}
