package org.leadlightdesigner.design.model;


public class Coord {

  // Small epsilon used for double value comparison.
  private static final double EPS = 1e-5;

  public final double x;
  public final double y;
  
  public Coord (double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }
  
  
  public boolean equals(Coord pt) {
    if (pt == null) {
      return false;
    } else {
      return Math.abs(x - pt.x) < EPS && Math.abs(y - pt.y) < EPS;
    }
  }

}
