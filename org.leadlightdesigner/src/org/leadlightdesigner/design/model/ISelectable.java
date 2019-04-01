package org.leadlightdesigner.design.model;

public interface ISelectable {

  public void setSelected(boolean selected);

  public boolean isSelected();

  public void toggleSelected();

  public boolean deselect();

  public void destroy();
  
}