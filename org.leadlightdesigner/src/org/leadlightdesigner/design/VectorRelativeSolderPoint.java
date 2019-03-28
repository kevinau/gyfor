package org.leadlightdesigner.design;

public class VectorRelativeSolderPoint extends RelativeSolderPoint {

  private final double length;
  
  private final double angleInDegrees;
  
  
  public VectorRelativeSolderPoint (SolderPoint startingPoint, double length, double angleInDegrees) {
    super (startingPoint);
    this.length = length;
    this.angleInDegrees = angleInDegrees;
  }
  
  
  @Override
  public DPoint getDPoint() {
    DPoint xy = getStartingPoint();
    return xy.getVectorOffset(length, angleInDegrees);
  }

}
