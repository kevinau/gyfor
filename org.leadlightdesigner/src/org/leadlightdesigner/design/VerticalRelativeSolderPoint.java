package org.leadlightdesigner.design;

public class VerticalRelativeSolderPoint extends RelativeSolderPoint {

  private final double offsetY;
  
  
  public VerticalRelativeSolderPoint (SolderPoint startingPoint, double offsetY) {
    super (startingPoint);
    this.offsetY = offsetY;
  }
  
  
  @Override
  public DPoint getDPoint() {
    DPoint xy = getStartingPoint();
    return xy.getDimensionOffset(0, offsetY);
  }

}
