package org.leadlightdesigner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.leadlightdesigner.design.model.Selectable;

public class GuidePoint extends Selectable {

  private static final int OVER_TOLERANCE = 5;
  
  private double x;
  private double y;

  private int useCount = 0;
  
  private boolean border;
  
  public GuidePoint (double x, double y) {
    this.x = x;
    this.y = y;
    this.border = false;
  }
  
  public GuidePoint (int x, int y, double zoom, boolean border) {
    this.x = x * zoom;
    this.y = y * zoom;
    this.border = border;
  }
  
  
  public GuidePoint (int x, int y, double zoom) {
    this(x, y, zoom, false);
  }
  
  
  public void incrementUse () {
    useCount++;
  }
  
  
  public void decrementUse () {
    useCount--;
  }
  
  
  public int getUseCount () {
    return useCount;
  }
  
  
  public double getX () {
    return x;
  }
  
  
  public int getX (double zoom) {
    return (int)(x / zoom + 0.5);
  }
  
  
  public double getY () {
    return y;
  }
  
  
  public int getY (double zoom) {
    return (int)(y / zoom + 0.5);
  }
  
  
  public boolean isOver (int x0, int y0, double zoom) {
    Rectangle range = new Rectangle(x0 - OVER_TOLERANCE, y0 - OVER_TOLERANCE, OVER_TOLERANCE * 2 + 1, OVER_TOLERANCE * 2 + 1);
 
    int x1 = getX(zoom);
    int y1 = getY(zoom);
    return range.contains(x1, y1);
  }
  
  
  public void drawOverWhite (Display display, GC gc, double zoom) {
    int x1 = getX(zoom);
    int y1 = getY(zoom);
    
    if (isSelected()) {
      gc.setLineWidth(4);
      gc.setForeground(new Color(display, 0, 187, 255));  // #00BBFF
      gc.drawRectangle(x1 - 4, y1 - 4, 8, 8);
    } else {
      gc.setLineWidth(2);
      gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
      gc.drawRectangle(x1 - 4, y1 - 4, 8, 8);
    }
  }
  
  
  public void drawOverImage (Display display, GC gc, double zoom) {
    int x1 = getX(zoom);
    int y1 = getY(zoom);
    
    gc.setLineWidth(2);
    gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
    gc.drawRectangle(x1 - 2, y1 - 2, 4, 4);
    gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    gc.drawRectangle(x1 - 4, y1 - 4, 8, 8);
    if (isSelected()) {
      gc.setLineWidth(3);
      gc.setForeground(new Color(display, 0, 187, 255));  // #00BBFF
      gc.drawRectangle(x1 - 7, y1 - 7, 14, 14);
    }
  }

  
  @Override
  public String toString () {
    return "[" + x + "," + y + (isSelected() ? ", selected" : "") + "]";
  }

}

