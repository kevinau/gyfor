package org.leadlightdesigner.design.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class DesignPoint extends Coord implements ISelectable {

  private static final int OVER_TOLERANCE = 5;

  private final DesignModel model;
  
  private List<DesignLine> attachedLines = new ArrayList<>();

  private transient boolean selected = false;

  DesignPoint(DesignModel model, double x, double y) {
    super(x, y);
    this.model = model;
  }

  DesignPoint(DesignModel model, int x, int y, double zoom) {
    super(x * zoom, y * zoom);
    this.model = model;
  }

  public void attachLine(DesignLine line) {
    attachedLines.add(line);
  }

  public void detachLine(DesignLine line) {
    attachedLines.remove(line);
  }

  public List<DesignLine> getAttachedLines() {
    return attachedLines;
  }

//  public double getX () {
//    return x;
//  }

  public int getX(double zoom) {
    return (int) (x / zoom + 0.5);
  }

//  public double getY () {
//    return y;
//  }

  public int getY(double zoom) {
    return (int) (y / zoom + 0.5);
  }

  public boolean isOver(int x0, int y0, double zoom) {
    int x1 = getX(zoom);
    int y1 = getY(zoom);
    return (x0 - OVER_TOLERANCE <= x1 && x1 <= x0 + OVER_TOLERANCE)
        && (y0 - OVER_TOLERANCE <= y1 && y1 <= y0 + OVER_TOLERANCE);
  }

  public void drawOverWhite(Display display, GC gc, double zoom) {
    int x1 = getX(zoom);
    int y1 = getY(zoom);

    if (isSelected()) {
      gc.setLineWidth(4);
      gc.setForeground(new Color(display, 0, 187, 255)); // #00BBFF
      gc.drawRectangle(x1 - 4, y1 - 4, 8, 8);
    } else {
      gc.setLineWidth(2);
      gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
      gc.drawRectangle(x1 - 4, y1 - 4, 8, 8);
    }
  }

  public void drawOverImage(Display display, GC gc, double zoom) {
    int x1 = getX(zoom);
    int y1 = getY(zoom);

    gc.setLineWidth(2);
    gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
    gc.drawRectangle(x1 - 2, y1 - 2, 4, 4);
    gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    gc.drawRectangle(x1 - 4, y1 - 4, 8, 8);
    if (isSelected()) {
      gc.setLineWidth(3);
      gc.setForeground(new Color(display, 0, 187, 255)); // #00BBFF
      gc.drawRectangle(x1 - 7, y1 - 7, 14, 14);
    }
  }

  @Override
  public String toString() {
    return "[" + x + "," + y + (isSelected() ? ", selected" : "") + "]";
  }

  @Override
  public void setSelected(boolean selected) {
    this.selected = selected;
    if (selected) {
      model.addSelectedItems(this);
    } else {
      model.removeSelectedItems(this);
    }
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

  
  public boolean isDetached() {
    return attachedLines.isEmpty();
  }
  
  
  @Override
  public void destroy() {
    if (attachedLines.isEmpty()) {
      model.removePoint(this);
    } else {
      throw new RuntimeException("Cannot destroy a point that is attached to lines");
    }
  }
  
}
