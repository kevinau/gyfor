package org.leadlightdesigner;

public interface TabItemChangeListener {

  public void contentModified (boolean dirty);
  
  public void nameChanged (String shortName, String extendedName);
  
}
