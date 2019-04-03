package org.leadlightdesigner.design.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;


public class DesignLine implements ISelectable {

  private static final int OVER_TOLERANCE = 2;

  private final DesignModel model;
  
  private LineType lineType = LineType.STRAIGHT;

  private DesignPoint startPoint;
  private DesignPoint endPoint;

  private boolean border = false;
  
  private transient boolean selected = false;
  

  DesignLine(DesignModel model, LineType lineType, DesignPoint startPoint, DesignPoint endPoint) {
    if (startPoint.equals(endPoint)) {
      throw new IllegalArgumentException("Cannot create a line whose start and end points are the same");
    }
    this.model = model;
    this.lineType = lineType;
    startPoint.attachLine(this);
    this.startPoint = startPoint;

    endPoint.attachLine(this);
    this.endPoint = endPoint;
  }

  
  DesignLine(DesignModel model, DesignPoint startPoint, DesignPoint endPoint) {
    this(model, LineType.STRAIGHT, startPoint, endPoint);
  }

  
  public LineType getLineType() {
    return lineType;
  }

  
  public void setLineType(LineType lineType) {
    this.lineType = lineType;
  }
  

  public DesignPoint getStartPoint() {
    return startPoint;
  }

  
  public DesignPoint getEndPoint() {
    return endPoint;
  }


  public boolean contains (DesignPoint p) {
    return startPoint.equals(p) || endPoint.equals(p);
  }
  
  
  public void destroy () {
    startPoint.detachLine(this);
    endPoint.detachLine(this);
    model.removeLine(this);
  }
  
//  public void destroyPointOnLine(DesignPoint point) {
//    List<DesignLine> use = point.getLineUse();
//    switch (use.size()) {
//    case 0 :
//    case 1 :
//      throw new RuntimeExeption("Point with zero or one connecting lines");
//    case 2 :
//      // TODO allow for the 16 possibilities of line types.  But for now, only consider straight lines.
//      DesignLine line1 = use.get(0);
//      DesignLine line2 = use.get(1);
//
//      DesignPoint p0;
//      DesignPoint p1;
//      if (line1.getStartPoint().equals(point)) {
//        p0 = line1.getEndPoint();
//      } else {
//        p0 = line1.getStartPoint();
//      }
//      model.destroyLine(line1);
//      
//      if (line2.getStartPoint().equals(point)) {
//        p1 = line2.getEndPoint();
//      } else {
//        p1 = line2.getStartPoint();
//      }
//      model.destroyLine(line1);
//      
//      DesignLine newLine = model.cloneLine(p0, p1);
//      model.destroyLine(line2);
//      model.destoryPoint(point);
//      break;
//    default :
//      throw new RuntimeException("Point is connected to three or more lines");
//    }
//  }


  // The following applies only to STRAIGHT lines
  public boolean isOver(int x, int y, double zoom) {
    DesignPoint p0 = getStartPoint();
    DesignPoint p1 = getEndPoint();
    
    int x0 = p0.getX(zoom);
    int y0 = p0.getY(zoom);
    int x1 = p1.getX(zoom);
    int y1 = p1.getY(zoom);

    double a2 = (x0 - x) * (x0 - x) + (y0 - y) * (y0 - y);
    double b2 = (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y);
    double c2 = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);

    if (a2 > b2 + c2) {
      // First angle is obtuse
      return false;
    }
    if (b2 > a2 + c2) {
      // Second angle is obtuse
      return false;
    }

    double a = Math.sqrt(a2);
    double b = Math.sqrt(b2);
    double c = Math.sqrt(c2);
    double base = c;

    double s;
    if (a < b) {
      // Swap a and b
      s = a;
      a = b;
      b = s;
    }
    if (b < c) {
      // Swap b and c
      s = b;
      b = c;
      c = s;
      if (a < b) {
        // The previous swap may require b to be propogated
        s = a;
        a = b;
        b = s;
      }
    }

    if (!(a >= b && a >= c && b >= c)) {
      throw new RuntimeException("Bad sort");
    }

    double heronArea = Math.sqrt((a + (b + c)) * (c - (a - b)) * (c + (a - b)) * (a + (b - c))) / 4;
    double h = (heronArea / base) / 2;
    return h < OVER_TOLERANCE;
  }

  
  // The following applies only to STRAIGHT lines
  public void drawOverWhite(Display display, GC gc, double zoom) {
    gc.setLineWidth(2);
    if (isSelected()) {
      gc.setForeground(new Color(display, 0, 187, 255)); // #00BBFF
      drawLine(display, gc, zoom);
    } else {
      gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
      drawLine(display, gc, zoom);
    }
  }

  
  // The following applies only to STRAIGHT lines
  private void drawLine(Display display, GC gc, double zoom) {
    DesignPoint p0 = getStartPoint();
    DesignPoint p1 = getEndPoint();
    
    int x0 = p0.getX(zoom);
    int y0 = p0.getY(zoom);
    int x1 = p1.getX(zoom);
    int y1 = p1.getY(zoom);

    Path p = new Path(display);
    switch (lineType) {
    case ARC:
      break;
    case CUBIC:
      break;
    case QUAD:
      break;
    case STRAIGHT:
      p.moveTo(x0, y0);
      p.lineTo(x1, y1);
      break;
    }
    gc.drawPath(p);
    p.dispose();
  }

  
  // The following applies only to STRAIGHT lines
  public Coord getNearestXY(int x, int y) {
    DesignPoint p0 = getStartPoint();
    DesignPoint p1 = getEndPoint();
    
    double x0 = p0.x;
    double y0 = p0.y;
    double x1 = p1.x;
    double y1 = p1.y;

    double a2 = (x0 - x) * (x0 - x) + (y0 - y) * (y0 - y);
    double b2 = (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y);
    double c2 = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0);

    double offset = (a2 - b2 + c2) / (2 * c2);
    double x2 = x0 + (x1 - x0) * offset;
    double y2 = y0 + (y1 - y0) * offset;

    return new Coord(x2, y2);
  }
  
  
  DesignLine setBorder(boolean border) {
    this.border = border;
    return this;
  }
  

  public boolean isBorder() {
    return border;
  }
  
  
  @Override
  public String toString() {
    String x = "Line[";
    x += startPoint;
    x += ",";
    x += endPoint;
    x += ",";
    x += isBorder() + "]";
    return x;
  }


  @Override
  public void setSelected(boolean selected) {
    this.selected = selected;
  }


  @Override
  public boolean isSelected() {
    return selected;
  }


  @Override
  public boolean deselect() {
    if (selected) {
      setSelected(false);
      return true;
    } else {
      return false;
    }
  }
  
  
  @Override
  public void toggleSelected() {
    setSelected(!selected);
  }


  public DesignPoint changeStartPoint(DesignPoint point) {
    DesignPoint priorStartPoint = startPoint;
    startPoint.detachLine(this);
    startPoint = point;
    startPoint.attachLine(this);
    return priorStartPoint;
  }
  
  
  public DesignPoint changeEndPoint(DesignPoint point) {
    DesignPoint priorEndPoint = endPoint;
    endPoint.detachLine(this);
    endPoint = point;
    endPoint.attachLine(this);
    return priorEndPoint;
  }


  @Override
  public void move(double deltaX, double deltaY) {
    startPoint.x += deltaX;
    startPoint.y += deltaY;
    endPoint.x += deltaX;
    endPoint.y += deltaY;
  }
  
}
