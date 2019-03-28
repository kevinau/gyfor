package org.leadlightdesigner.design;

public class ManualSolderPoint extends SolderPoint {
  
  private double x;
  private double y;
  
  public ManualSolderPoint (double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  
  @Override
  public DPoint getDPoint() {
    return new DPoint(x, y);
  }

}
