package org.leadlightdesigner.design;

public class HorizontalRelativeSolderPoint extends RelativeSolderPoint {

  private final double offsetX;
  
  
  public HorizontalRelativeSolderPoint (SolderPoint startingPoint, double offsetX) {
    super (startingPoint);
    this.offsetX = offsetX;
  }
  
  
  @Override
  public DPoint getDPoint() {
    DPoint xy = getStartingPoint();
    return xy.getDimensionOffset(offsetX, 0);
  }

}
