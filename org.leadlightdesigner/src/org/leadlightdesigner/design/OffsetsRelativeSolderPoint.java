package org.leadlightdesigner.design;

public class OffsetsRelativeSolderPoint extends RelativeSolderPoint {

  private final double offsetX;
  
  private final double offsetY;
  
  
  public OffsetsRelativeSolderPoint (SolderPoint startingPoint, double offsetX, double offsetY) {
    super (startingPoint);
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }
  
  
  @Override
  public DPoint getDPoint() {
    DPoint xy = getStartingPoint();
    return xy.getDimensionOffset(offsetX, offsetY);
  }

}
