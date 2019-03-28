package org.leadlightdesigner.design.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;


public class DesignPoint extends Coord implements ISelectable {

  private static final int OVER_TOLERANCE = 5;
  
  private List<DesignLine> lineUse = new ArrayList<>();
  
  private transient boolean selected = false;
  

  DesignPoint (double x, double y) {
    super (x, y);
  }
  
  DesignPoint (int x, int y, double zoom) {
    super (x * zoom, y * zoom);
  }
  
  
  public void addLineUse (DesignLine line) {
    lineUse.add(line);
  }
  
  
  public void removeLineUse (DesignLine line) {
    lineUse.remove(line);
  }
  
  
  public List<DesignLine> getLineUse () {
    return lineUse;
  }
  
  
//  public double getX () {
//    return x;
//  }
  
  
  public int getX (double zoom) {
    return (int)(x / zoom + 0.5);
  }
  
  
//  public double getY () {
//    return y;
//  }
  
  
  public int getY (double zoom) {
    return (int)(y / zoom + 0.5);
  }
  
  
  public boolean isOver (int x0, int y0, double zoom) {
    int x1 = getX(zoom);
    int y1 = getY(zoom);
    return (x0 - OVER_TOLERANCE <= x1 && x1 <= x0 + OVER_TOLERANCE) && 
           (y0 - OVER_TOLERANCE <= y1 && y1 <= y0 + OVER_TOLERANCE);
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

  
  @Override
  public void setSelected(boolean selected) {
    this.selected = selected;
  }


  @Override
  public boolean isSelected() {
    return selected;
  }


  @Override
  public void toggleSelected() {
    this.selected = !selected;
  }
  
}

