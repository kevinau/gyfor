package org.leadlightdesigner.guide;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.leadlightdesigner.GuidePoint;

import georegression.struct.point.Point2D_F64;


public class GuideAttributes {

  public static final String NAME = "design.guide";
  
  private final String sourcePathName;
  
  private int targetWidth = 841;
  
  private int targetHeight = 465;
  
  private double[] flattenedPoints = new double[8];
  
  
  public GuideAttributes () {
    sourcePathName = null;
  }
  
  
  public GuideAttributes (Path sourcePath) {
    if (sourcePath == null) {
      throw new NullPointerException("sourcePath");
    }
    this.sourcePathName = sourcePath.toString();
  }

  
  public GuideAttributes (Path sourcePath, int targetWidth, int targetHeight, List<GuidePoint> points) {
    if (sourcePath == null) {
      throw new NullPointerException("sourcePath");
    }
    this.sourcePathName = sourcePath.toString();
    this.targetWidth = targetWidth;
    this.targetHeight = targetHeight;
    
    this.flattenedPoints = new double[8];
    int i = 0;
    for (GuidePoint p : points) {
      this.flattenedPoints[i++] = p.getX();
      this.flattenedPoints[i++] = p.getY();
    }
  }

  
  public Path getSource () {
    return Paths.get(sourcePathName);
  }
  

  public int getTargetWidth () {
    return targetWidth;
  }
  
  
  public int getTargetHeight () {
    return targetHeight;
  }
  
  
//  public void setCoords (Point2D_F64[] coords) {
//    int i = 0;
//    this.points = new double[8];
//    for (Point2D_F64 coord : coords) {
//      this.points[i++] = coord.getX();
//      this.points[i++] = coord.getY();
//    }
//  }
  
  public Point2D_F64[] getCoords () {
    Point2D_F64[] points2d = new Point2D_F64[flattenedPoints.length / 2];
    for (int i = 0; i < points2d.length; i++) {
      points2d[i] = new Point2D_F64(flattenedPoints[i * 2], flattenedPoints[i * 2 + 1]);
    }
    return points2d;
  }
  
}
