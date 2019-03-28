package org.leadlightdesigner.design;

public class DPoint {

  private double x;
  private double y;
  
  public DPoint (double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  
  public DPoint getVectorOffset (double length, double angleInDegrees) {
    double a2 = Math.toRadians(angleInDegrees);
    double x = this.x + Math.cos(a2);
    double y = this.y + Math.sin(a2);
    return new DPoint(x, y);
  }
  
  
  public DPoint getDimensionOffset (double x, double y) {
    return new DPoint(this.x + x, this.y + y);
  }
  
  
  public double lengthTo (DPoint end) {
    return Math.hypot(this.x - end.x, this.y - end.y);
  }
  
  public double getVectorAngle (DPoint end) {
    return Math.atan2(end.y - y, end.x - x);
  }
  
  
  public float getX() {
    return (float)x;
  }
  
  public float getY() {
    return (float)y;
  }
  
  
  @Override
  public String toString() {
    return "DPoint[" + x + "," + y + "]";
  }
  
}
