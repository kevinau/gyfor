package org.leadlightdesigner.guide;

import java.util.Arrays;
import java.util.List;

import org.leadlightdesigner.GuidePoint;

public class GuidePoints {

  private static class AngledGuidePoint implements Comparable<AngledGuidePoint> {
  
    private double angle;
    private GuidePoint point;
    
    public AngledGuidePoint(double angle, GuidePoint point) {
      this.angle = angle;
      this.point = point;
    }

    @Override
    public int compareTo(AngledGuidePoint o) {
      return Double.compare(angle, o.angle);
    }

  }

  
  public static GuidePoint[] sortByAngle (List<GuidePoint> coords) {
    AngledGuidePoint[] newPoints = new AngledGuidePoint[4];
    
    double sumX = 0;
    double sumY = 0;
    for (GuidePoint c : coords) {
      sumX += c.getX();
      sumY += c.getY();
    }
    
    double aveX = sumX / 4;
    double aveY = sumY / 4;
    
    int i = 0;
    for (GuidePoint c : coords) {
      double angle = Math.atan2(-(c.getY() - aveY), -(c.getX() - aveX));
      if (angle < 0.0) {
        angle = 2 * Math.PI + angle;
      }
      newPoints[i] = new AngledGuidePoint(angle, c);
      i++;
    }
    
    Arrays.sort(newPoints);
    
    GuidePoint[] px = new GuidePoint[4];
    for (i = 0; i < 4; i++) {
      px[i] = newPoints[i].point;
    }
    return px;
  }
}
