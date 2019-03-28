package org.leadlightdesigner.design;

import org.leadlightdesigner.design.model.LineType;

public class PointToPointLine implements ICameLine {

  private LineType lineType = LineType.STRAIGHT;
  
  private SolderPoint startPoint;
  
  private SolderPoint endPoint;
  
  private double angle0;
  
  private double length0;
  
  
  public PointToPointLine () {
  }
  
  
  public PointToPointLine (LineType lineType, SolderPoint startPoint, SolderPoint endPoint) {
    this.lineType = lineType;
    this.startPoint = startPoint;
    this.endPoint = endPoint;
  }
  
  
  public static PointToPointLine newStraight (SolderPoint startPoint, SolderPoint endPoint) {
    PointToPointLine line = new PointToPointLine(LineType.STRAIGHT, startPoint, endPoint);
    return line;
  }

  
  
  public static PointToPointLine newQuad (SolderPoint startPoint, SolderPoint endPoint, double angle0, double length0) {
    PointToPointLine line = new PointToPointLine(LineType.QUAD, startPoint, endPoint);
    line.angle0 = Math.toRadians(angle0);
    line.length0 = length0;
    return line;
  }

  
  
  public LineType getLineType() {
    return lineType;
  }
  
  
  public void setLineType (LineType lineType) {
    this.lineType = lineType;
  }
  
  
  @Override
  public SolderPoint getStartPoint () {
    return startPoint;
  }
  

  @Override
  public SolderPoint getEndPoint () {
    return endPoint;
  }
  
  
  public DPoint getControlPoint0 () {
    DPoint p0 = startPoint.getDPoint();
    DPoint p1 = endPoint.getDPoint();
    double lineAngle = p0.getVectorAngle(p1);
    double ctrlAngle = lineAngle + angle0;
    return new DPoint(length0 * Math.cos(ctrlAngle), length0 * Math.sin(ctrlAngle));
  }
  
  private static final double TOLERANCE = 50;
  private static final double DEG90 = Math.PI / 2;
  
  public boolean isNear(int x0, int y0) {
    DPoint p1 = startPoint.getDPoint();
    DPoint p2 = endPoint.getDPoint();

    switch (lineType) {
    case STRAIGHT :
      DPoint p0 = new DPoint(x0, y0);
      
      double x1 = p1.getX();
      double y1 = p1.getY();
      double x2 = p2.getX();
      double y2 = p2.getY();
      
      double dem = p1.lengthTo(p2);
      double num = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1);
      double distance = num / dem;
      if (distance > TOLERANCE) {
        return false;
      }
      
      double a1 = Math.abs(p1.getVectorAngle(p2) - p1.getVectorAngle(p0));
      int  q1 = ((int)(a1 / DEG90)) % 4;
      if (q1 == 1 || q1 == 2) {
        return false;
      }
      
      double a2 = Math.abs(p2.getVectorAngle(p1) - p2.getVectorAngle(p0));
      int  q2 = ((int)(a2 / DEG90)) % 4;
      if (q2 == 1 || q2 == 2) {
        return false;
      }
      return true;
    case ARC:
      break;
    case CUBIC:
      break;
    case QUAD:
      break;
    }
    return false;
  }
  
  public void dump () {
    System.out.println(lineType + ": " + startPoint + " " + endPoint + " " + angle0 + " " + length0);
  }
}
