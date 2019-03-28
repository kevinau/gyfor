package org.leadlightdesigner.design.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.gyfor.nio.SafeWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DesignModel {

  public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  //private Coord[] borderCoords;
  private DesignLine[] borderLines;
  
  private List<DesignPoint> points = new ArrayList<>();
  private List<DesignLine> lines = new ArrayList<>();

  
  public DesignModel (Coord... xy) {
    //this.borderCoords = xy;
    
    // Start with border lines
    for (int j = 0; j < xy.length; j++) {
      DesignPoint point = new DesignPoint(xy[j].x, xy[j].y);
      points.add(point);
    }
    
    borderLines = new DesignLine[points.size()];
    
    int i = 0;
    while (i < points.size()) {
      int i1 = (i + 1 < points.size() ? i + 1 : 0);
      
      DesignPoint p0 = points.get(i);
      DesignPoint p1 = points.get(i1);
      DesignLine line = new DesignLine(this, p0, p1).setBorder();
      lines.add(line);
      borderLines[i] = line;
      i++;
    }
  }
  
  
  public List<DesignLine> getBorderLines() {
    List<DesignLine> border = new ArrayList<>();
    for (DesignLine line : lines) {
      if (line.isBorder()) {
        border.add(line);
      }
    }
    return border;
  }
  
  
  public List<DesignPoint> getPoints() {
    return points;
  }
  
  
  public List<DesignLine> getLines() {
    return lines;
  }
  
  
  public DesignLine createLine (DesignPoint startPoint, DesignPoint endPoint) {
    DesignLine line = new DesignLine(this, LineType.STRAIGHT, startPoint, endPoint);
    lines.add(line);
    return line;
  }

  
  public void destroyLine (DesignLine line) {
    for (DesignPoint p : line.getPoints()) {
      p.removeLineUse(line);
    }
    lines.remove(line);
  }
  
  
  public boolean isNearSelectable (int x, int y, double zoom) {
    for (DesignPoint point : points) {
      if (point.isOver(x, y, zoom)) {
        return true;
      }
    }
    for (DesignLine line : lines) {
      if (line.isOver(x, y, zoom)) {
        return true;
      }
    }
    return false;
  }
  
  
  public DesignPoint getSingleSelectedPoint () {
    DesignPoint found = null;
    for (DesignPoint p : points) {
      if (p.isSelected()) {
        if (found != null) {
          // More than one point found
          return null;
        }
        found = p;
      }
    }
    return found;
  }


  private boolean pointInUse (DesignPoint p) {
    for (DesignLine x : lines) {
      if (x.isBorder()) {
        DesignPoint sp = x.getStartPoint();
        if (sp == p) {
          return true;
        }
        sp = x.getEndPoint();
        if (sp == p) {
          return true;
        }  
      }
      if (!x.isSelected()) {
        DesignPoint sp = x.getStartPoint();
        if (sp == p && !sp.isSelected()) {
          return true;
        }
        sp = x.getEndPoint();
        if (sp == p && !sp.isSelected()) {
          return true;
        }
      }
    }
    return false;
  }

  
  public boolean lineInUse (DesignLine x) {
    List<DesignPoint> px = x.getPoints();
    int n = px.size();
    for (int i = 1; i < n - 1; i++) {
      if (pointInUse(px.get(i))) {
        return true;
      }
    }
    return false;
  }

  
  public void refreshPoints () {
    points = new ArrayList<>();
    for (DesignLine x : lines) {
      for (DesignPoint p : x.getPoints()) {
        if (!points.contains(p)) {
          points.add(p);
        }
      }
    }
  }
  
  
  public void validateModel () {
    for (DesignLine x : lines) {
      if (x.getPoints().size() < 2) {
        throw new RuntimeException("Line " + x + " model error");
      }
    }
  }
  
  
  public void toggleSelection (ISelectable item) {
    for (ISelectable p : points) {
      if (p == item) {
        p.toggleSelected();
      } else {
        p.setSelected(false);
      }
    }
    for (ISelectable x : lines) {
      if (x == item) {
        x.toggleSelected();
      } else {
        x.setSelected(false);
      }
    }
  }


  public void selectOnly (ISelectable item) {
    for (ISelectable p : points) {
      if (p == item) {
        p.setSelected(true);
      } else {
        p.setSelected(false);
      }
    }
    for (ISelectable x : lines) {
      if (x == item) {
        x.setSelected(true);
      } else {
        x.setSelected(false);
      }
    }
  }
  
  
  public boolean deselectAllLines () {
    boolean modified = false;
    for (ISelectable x : lines) {
      if (x.isSelected()) {
        x.setSelected(false);
        modified = true;
      }
    }
    return modified;
  }

  
  public DesignPoint getOverPoint (int x, int y, double zoom) {
    for (DesignPoint point : points) {
      if (point.isOver(x, y, zoom)) {
        return point;
      }
    }
    return null;
  }

  
  public DesignLine getOverLine (int x, int y, double zoom) {
    for (DesignLine line : lines) {
      if (line.isOver(x, y, zoom)) {
        return line;
      }
    }
    return null;
  }

  
  public List<ISelectable> getOverItems (int x, int y, double zoom) {
    List<ISelectable> sx = new ArrayList<>();
    for (DesignPoint point : points) {
      if (point.isOver(x, y, zoom)) {
        sx.add(point);
      }
    }
    for (DesignLine line : lines) {
      if (line.isOver(x, y, zoom)) {
        sx.add(line);
      }
    }
    return sx;
  }
  

  public boolean deleteSelectedItems () {
    boolean modified = false;
    
    int i = 0;
    while (i < lines.size()) {
      DesignLine line = lines.get(i);
      if (line.isSelected() && lineInUse(line) == false) {
        lines.remove(i);
        modified = true;
      } else {
        List<DesignPoint> points = line.getPoints();
        int j = 0;
        while (j < points.size()) {
          DesignPoint p = points.get(j);
          if (p.isSelected() && pointInUse(p) == false) {
            line.destroyPoint(p);
            modified = true;
          } else {
            j++;
          }
        }
        if (line.getPoints().size() < 2) {
          lines.remove(i);
          modified = true;
        } else {
          i++;
        }
      }
    }
    if (modified) {
      refreshPoints();
    }
    return modified;
  }
  
  
  public void dump () {
    int i = 0;
    for (DesignPoint p : points) {
      System.out.println("P" + i + " (" + p.x + "," + p.y + ")");
      i++;
    }
    
    int j = 0;
    for (DesignLine line : lines) {
      System.out.print("L" + j + ": " + line.getLineType());
      for (DesignPoint p : line.getPoints()) {
        i = points.indexOf(p);
        System.out.print(",P" + i);
      }
      System.out.println();
      j++;
    }
  }


  public DesignPoint createPoint(double x, double y) {
    DesignPoint p = new DesignPoint(x, y);
    points.add(p);
    return p;
  }
  
  
  public static void save(DesignModel model, File file) {
    try (SafeWriter writer = new SafeWriter(file.toPath())) {
      gson.toJson(model, writer);
      writer.commit();
    }
  }

  
  public static DesignModel load(File file) throws FileNotFoundException {
    Reader reader = new FileReader(file);
    DesignModel model = gson.fromJson(reader, DesignModel.class);
    return model;
  }

  
  /**
   * crossing number test for a point in a polygon
   * Input:   point = a point,
   *          path[] = vertex points of a polygon
   *   Return:  0 = outside, 1 = inside
   * This code is patterned after [Franklin, 2000]
   */
  public static boolean withinPath (double x, double y, DesignLine[] path) {
    int cn = 0;    // the  crossing number counter

//    // loop through all edges of the polygon path
//    for (int i = 0; i < path.length; i++) {    // edge from path[i]  to path[i+1]
//      int i1 = (i + 1 < path.length ? i + 1 : 0);
//      
//      if (((path[i].y <= y) && (path[i1].y > y))        // an upward crossing
//          || ((path[i].y > y) && (path[i1].y <= y))) {  // a downward crossing
//        // compute  the actual edge-ray intersect x-coordinate
//        double vt = (y - path[i].y) / (path[i1].y - path[i].y);
//        double xx = path[i].x + vt * (path[i1].x - path[i].x);
//        System.out.println("************ valid crossing point " + xx + "," + y);
//        if (x <  path[i].x + vt * (path[i1].x - path[i].x)) {
//          // x < intersect
//          cn++;   // a valid crossing of y=point y right of point x
//        }
//      }
//    }
    
    // loop through all edges of the polygon path
    for (DesignLine line : path) {
      DesignPoint p0 = line.getStartPoint();
      DesignPoint p1 = line.getEndPoint();

      if (((p0.y <= y) && (p1.y > y))        // an upward crossing
          || ((p0.y > y) && (p1.y <= y))) {  // a downward crossing
        // compute  the actual edge-ray intersect x-coordinate
        double vt = (y - p0.y) / (p1.y - p0.y);
        double xx = p0.x + vt * (p1.x - p0.x);
        if (x <  p0.x + vt * (p1.x - p0.x)) {
          // x < intersect
          cn++;   // a valid crossing of y=point y right of point x
        }
      }
    }

    return (cn & 1) == 1;    // 0 if even (out), and 1 if  odd (in)
  }
  
  
  public boolean withinBorder (double x, double y) {
    return withinPath(x, y, borderLines);
  }
  
  
  private Coord pathIntersect (DesignPoint p3, Coord p4, DesignLine[] lines) {
    // Loop through all edges of the polygon path
    // Because x3,y4 is within the polygon path, and x4,y4 is outside the polygon path,
    // there is only one intersection point.
    int i = 0;
    
    for (DesignLine line : lines) {
      if (line.contains(p3)) {
        // The line contains the selected point, so there is no intersection
        continue;
      }

      // The following is our segment
      DesignPoint p0 = line.getStartPoint();
      DesignPoint p1 = line.getEndPoint();
      
      double x1 = p0.x;
      double y1 = p0.y;
      double x2 = p1.x;
      double y2 = p1.y;
      double x3 = p3.x;
      double y3 = p3.y;
      double x4 = p4.x;
      double y4 = p4.y;
      
      double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
      if (d == 0) {
        // The line and the segment is parallel or coincident, so no intersecting point
      } else {
        double t0 = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4));
        double t = t0 / d;
        double u0 = ((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3));
        double u = u0 / d;
        if (t < 0.0 || t > 1.0 || u < 0.0 || u > 1.0) {
          // t is < 0, so the line does not intersect the edge
        } else {
          // The line intersects the edge
          double x = x1 + t * (x2 - x1);
          double x9 = x3 + u * (x4 - x3);
          double y = y1 + t * (y2 - y1);
          double y9 = y3 + u * (y4 - y3);
          return new Coord(x, y);
        }
      }
    }
    return null;
  }
  
  
  public Coord pathIntersect2 (DesignPoint p3, Coord p4, DesignLine[] lines) {
    // Loop through all edges of the polygon path
    // x3,y3 is the selected DesignPoint.  x4,y4 is the cursor position
    // Because x3,y3 is within the polygon path, and x4,y4 is outside the polygon path,
    // there is only one intersection point.
    for (DesignLine line : lines) {
      if (line.contains(p3)) {
        // The line contains the selected point, so there is no intersection
        continue;
      }

      // The following is our segment
      DesignPoint p0 = line.getStartPoint();
      DesignPoint p1 = line.getEndPoint();
      
//      double x1 = p0.x;
//      double y1 = p0.y;
//      double x2 = p1.x;
//      double y2 = p1.y;
//      if ((x1 == x3 && y1 == y3) || (x2 == x3 && y2 == y3)) {
//        // The segment has an end point that is the same as the selectedDesignPoint
//        // There is no intersection 
//        continue;
//      }
      
      Coord intersect = LineSegmentIntersection.lineSegmentIntersection1(p0, p1, p3, p4);
      if (intersect != null) {
        return intersect;
      }
    }
    return null;
  }
  
  
  public Coord borderIntersect (DesignPoint p3, Coord p4) {
    return pathIntersect2(p3, p4, borderLines);
  }
  
}
