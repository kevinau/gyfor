package org.leadlightdesigner.design;

public abstract class RelativeSolderPoint extends SolderPoint {
  
  private final SolderPoint startingPoint;
  
  protected RelativeSolderPoint (SolderPoint startingPoint) {
    this.startingPoint = startingPoint;
  }
  
  
  protected DPoint getStartingPoint () {
    return startingPoint.getDPoint();
  }
  
}
