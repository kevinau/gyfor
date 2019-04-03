package org.leadlightdesigner.design.model;

public interface PointChangeListener {

  public void pointSelectionChange(DesignPoint point, boolean selected);
  
  public void pointMoved(DesignPoint point, double deltaX, double deltaY);
  
}
