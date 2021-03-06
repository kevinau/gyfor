package org.leadlightdesigner.design;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.leadlightdesigner.design.model.Coord;
import org.leadlightdesigner.design.model.DesignLine;
import org.leadlightdesigner.design.model.DesignModel;
import org.leadlightdesigner.design.model.DesignPoint;
import org.leadlightdesigner.design.model.ISelectable;
import org.leadlightdesigner.design.model.ModelChangeListener;


public class DesignCanvas extends Canvas implements PaintListener, MouseListener, MouseMoveListener, MouseTrackListener, KeyListener {

  private static final int MOVE_DELTA = 12;
  
  private class DoubleClickTask implements Runnable {
    private final DesignLine line;
    private final int x;
    private final int y;
    
    private DoubleClickTask (DesignLine line, int x, int y) {
      if (line == null) {
        throw new IllegalArgumentException("Design line cannot be null");
      }
      this.line = line;
      this.x = x;
      this.y = y;
    }
    
    @Override
    public void run () {
      // Add a new point
      DesignPoint newPoint = createPointOnLine(line, x, y);
      model.selectOnly (newPoint);
      model.dump();
      redraw();
    }
     
  }
  
  
  private class DrawDoubleClick implements Runnable {
    private final DesignPoint point;
    
    private DrawDoubleClick (DesignPoint point) {
      this.point = point;
    }
    
    @Override
    public void run () {
      // Deselect point
      point.setSelected(false);
      redraw();
    }
     
  }
  
  
  private final Cursor exitCursor;
  private final Cursor overNothingCursor;
  private final Cursor overSomethingCursor;
  private final Cursor lineDrawCursor;
  private final Cursor lineDrawActiveCursor;
  
  private final GC gc;
  
  private DesignModel model;
  
  private double zoom = 1.0;
  
  private Runnable doubleClickTask = null;
  
  private enum Mode {
    NORMAL,
    LINE_DRAW;
  }
  
  private Mode mode = Mode.NORMAL;
  
  
  public DesignCanvas(Composite parent) {
    super(parent, SWT.DOUBLE_BUFFERED);
    
    exitCursor = parent.getCursor();
    overNothingCursor = getCursor(parent.getDisplay(), "cursor-white-32.png", 9, 3);
    overSomethingCursor = getCursor(parent.getDisplay(), "cursor-cyan-32.png", 9, 3);
    lineDrawCursor = getCursor(parent.getDisplay(), "cursor-line-32.png", 9, 3);
    lineDrawActiveCursor = getCursor(parent.getDisplay(), "cursor-line-active-32.png", 9, 3);

    Display display = parent.getDisplay();
    gc = new GC(display.getActiveShell());

    Rectangle displaySize = display.getClientArea();
    setSize(displaySize.width / 2, displaySize.height / 2);

    setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

    addPaintListener(this);
    addMouseListener(this);
    addMouseMoveListener(this);
    addMouseTrackListener(this);
    addKeyListener(this);

    // TODO remove the test setup
    testSetup();
    
    setFocus();
  }
  
  
  private static Cursor getCursor (Display display, String imageFileName, int x, int y) {
    Image whitePointer = new Image(display, Snippet92.class.getResource("/").getPath() + imageFileName);
    return new Cursor(display, whitePointer.getImageData(), x, y);
  }
  
  
  public void addModifyChangeListener (ModelChangeListener x) {
    model.addModelChangeListener(x);
  }
  
  
  public void removeModifyChangeListener (ModelChangeListener x) {
    model.removeModelChangeListener(x);
  }
  
  
  public void testSetup () {
    int offset = 50;
    model = new DesignModel(
        new Coord (offset + 0, offset + 0),
        new Coord (offset + 400, offset + 0),
        new Coord (offset + 400, offset + 300),
        new Coord (offset + 0, offset + 300));

    //model.dump();
    List<DesignLine> border = model.getBorderLines();

    DesignLine right = border.get(1);
    DesignLine left = border.get(3);
    DesignPoint rightEndPoint = right.getEndPoint();
    
    DesignPoint p5 = model.createPointOnLine(right, offset + 400, offset + 100);
    DesignPoint p6 = model.createPointOnLine(left, offset + 0, offset + 100);

    DesignLine l1 = model.createLine(p5, p6, false);
    DesignLine l2 = model.createLine(p6, rightEndPoint, false);
    //model.dump();
  }
  
  
  @Override
  public void paintControl(PaintEvent ev) {
    for (DesignLine x : model.getLines()) {
      x.drawOverWhite(ev.display, ev.gc, zoom);
    }
    for (DesignPoint p : model.getPoints()) {
      p.drawOverWhite(ev.display, ev.gc, zoom);
    }
    if (mode == Mode.LINE_DRAW) {
      DesignPoint p = model.getSingleSelectedPoint();
      if (p != null) {
        Point cursorPoint = toControl(ev.display.getCursorLocation());
        ev.gc.setLineWidth(1);
        ev.gc.setLineStyle(SWT.LINE_DASH);
        ev.gc.setForeground(ev.display.getSystemColor(SWT.COLOR_DARK_GRAY));
        if (model.withinBorder(cursorPoint.x * zoom, cursorPoint.y * zoom)) {
          ev.gc.drawLine(p.getX(zoom), p.getY(zoom), cursorPoint.x, cursorPoint.y);
        } else {
          Coord xy = model.borderIntersect(p, new Coord(cursorPoint.x, cursorPoint.y));
          if (xy != null) {
            ev.gc.drawLine(p.getX(zoom), p.getY(zoom), (int)(xy.x), (int)(xy.y));
          }
        }
      }
    }
  }

  
  @Override
  public void mouseUp(MouseEvent ev) {
  }

  
  @Override
  public void mouseDoubleClick(MouseEvent ev) {
    if (doubleClickTask != null) {
      doubleClickTask.run();
      doubleClickTask = null;
    }
  }

  
  private DesignPoint createPointOnLine (DesignLine line, int x, int y) {
    Coord xy = line.getNearestXY(x, y);
    DesignPoint newPoint = model.createPointOnLine(line, xy.x, xy.y);
    return newPoint;
  }
    
  
  @Override
  public void mouseDown(MouseEvent ev) {
    if (ev.button == 1) {
      switch (mode) {
      case LINE_DRAW :
        DesignPoint p = model.getSingleSelectedPoint();
        if (p != null) {
          DesignPoint overPoint = model.getOverPoint(ev.x, ev.y, zoom);
          if (overPoint != null) {
            if (!overPoint.equals(p)) {
              model.createLine(p, overPoint, false);
              model.selectOnly(overPoint);
            }
            registerDoubleClickTask(new DrawDoubleClick(overPoint));
          } else {
            DesignLine overLine = model.getOverLine(ev.x, ev.y, zoom);
            if (overLine != null) {
              DesignPoint newPoint = createPointOnLine(overLine, ev.x, ev.y);
              model.createLine(p, newPoint, false);
              model.selectOnly (newPoint);
              registerDoubleClickTask(new DrawDoubleClick(newPoint));
            } else {
              model.selectOnly(null);
            }
          }
        } else {
          ISelectable overItem = model.getOverPoint(ev.x, ev.y, zoom);
          model.toggleSelection(overItem);
        }
        redraw();
        break;
      case NORMAL :
        List<ISelectable> overItems = model.getOverItems(ev.x, ev.y, zoom);
//        switch (overItems.size()) {
//        case 0:
//          model.selectOnly(null);
//          break;
//        case 1:
//          ISelectable overItem = overItems.get(0);
//          if (overItem instanceof DesignLine) {
//            DesignLine overLine = (DesignLine) overItem;
//            //if (overLine.isBorder()) {
//            //  model.selectOnly(null);
//            //} else {
//              model.toggleSelection(overItem);
//            //}
//            registerDoubleClickTask (new DoubleClickTask(overLine, ev.x, ev.y));
//          } else {
//            model.toggleSelection(overItem);
//          }
//          break;
//        default:
          ISelectable found = null;
          for (ISelectable item : overItems) {
            if (item instanceof DesignPoint) {
              found = item;
              break;
            }
          }
          if (found == null && overItems.size() > 0) {
            found = overItems.get(0);
          }
          if (((ev.stateMask & SWT.SHIFT)) != 0) {
            model.selectToggleCumulative(found);
          } else {
            model.selectOnly(found);
            if (found != null && (found instanceof DesignLine)) {
              registerDoubleClickTask (new DoubleClickTask((DesignLine)found, ev.x, ev.y));
            }
          }
//          selectNextItem(overItems);
//          break;
//        }
        redraw();
        break;
      }
    }

    
//    Display.getDefault().timerExec(Display.getDefault().getDoubleClickTime(), new Runnable() {
//      @Override
//      public void run() {
//        if (!doubleClick) {
//          System.out.println("Single Click! (2)");
//        }
//      }
//    });
  }

  
  private void registerDoubleClickTask (Runnable task) {
    doubleClickTask = task;
    Display.getDefault().timerExec(Display.getDefault().getDoubleClickTime(), new Runnable() {
      @Override
      public void run() {
        doubleClickTask = null;
      }
    });
  }
  
  
  private void selectNextItem (List<ISelectable> items) {
    // Find currently selected item, if any
    int i = 0;
    while (i < items.size()) {
      if (items.get(i).isSelected()) {
        i++;
        break;
      }
      i++;
    }
    if (i == items.size()) i = 0;
    ISelectable item = items.get(i);
    model.selectOnly(item);
    
//    if (i < items.size()) {
//      // Deselect the selected item
//      items.get(i).setSelected(false);
//      
//      // Find next (assumed to be unselected) that is not a border line
//      int start = i;
//      i++;
//      if (i == items.size()) i = 0;
//      while (i != start) {
//        ISelectable item = items.get(i);
//        if (!(item instanceof DesignLine && ((DesignLine)item).isBorder())) {
//          model.selectOnly(item);
//          break;
//        }
//        i++;
//        if (i == items.size()) i = 0;
//      }
//    } else {
//      // No currently selected item, so select the first non border line
//      for (ISelectable item : items) {
//        if (!(item instanceof DesignLine && ((DesignLine)item).isBorder())) {
//          model.selectOnly(item);
//          break;
//        }
//      }
//    }
  }
  
  
  @Override
  public void mouseMove(MouseEvent ev) {
    setCursor (ev.x, ev.y);
    if (mode == Mode.LINE_DRAW) {
      // In addition to setting the cursor, we need to draw the rubber-band line
      redraw();
    }
  }
  
  
  @Override
  public void mouseEnter(MouseEvent ev) {
    mouseMove (ev);
  }

  
  @Override
  public void mouseExit(MouseEvent ev) {
    getShell().setCursor(exitCursor);
  }

  
  @Override
  public void mouseHover(MouseEvent ev) {
    // TODO Auto-generated method stub
  }
    
  
  @Override
  public void keyPressed(KeyEvent ev) {
    System.out.println("key code " + ev.keyCode);
    
    switch (ev.keyCode) {
    case SWT.ARROW_LEFT :         // Left arrow
      moveSelected(-MOVE_DELTA, 0);
      break;
    case SWT.ARROW_UP :          // Up arrow
      moveSelected(0, -MOVE_DELTA);
      break;
    case SWT.ARROW_RIGHT :       // Right arrow
      moveSelected(+MOVE_DELTA, 0);
      break;
    case SWT.ARROW_DOWN :        // Down arrow
      moveSelected(0, +MOVE_DELTA);
      break;
    case 97 :                   // Toggle line draw mode (key A)
      switch (mode) {
      case LINE_DRAW :
        mode = Mode.NORMAL;
        break;
      case NORMAL :
        mode = Mode.LINE_DRAW;
        deselectAllLines();
      }
      Point cursorLocation = toControl(ev.display.getCursorLocation());
      //model.getOverPoint(cursorLocation.x, cursorLocation.y, zoom);
      setCursor (cursorLocation.x, cursorLocation.y);
      // Draw or clear the rubber-band line
      redraw();
      break;
    case 127 :                  // Delete selected items
      boolean itemsDeleted = model.deleteSelectedItems();
      if (itemsDeleted) {
        redraw();
      }
      break;
    }
  }

  @Override
  public void keyReleased(KeyEvent ev) {
  }
  
  
  private void setCursor (int x, int y) {
    switch (mode) {
    case LINE_DRAW :
      boolean nearSelectable = model.isNearSelectable(x, y, zoom);
      if (nearSelectable) {
        getShell().setCursor(lineDrawActiveCursor);
      } else {
        if (model.withinBorder((float)(x * zoom), (float)(y * zoom))) {
          getShell().setCursor(lineDrawCursor);
        } else {
          getShell().setCursor(overNothingCursor);
        }
      }
      break;
    case NORMAL :
      boolean nearSelectable2 = model.isNearSelectable(x, y, zoom);
      if (nearSelectable2) {
        getShell().setCursor(overSomethingCursor);
      } else {
        getShell().setCursor(overNothingCursor);
      }
      break;
    }
  }

  
  private void moveSelected(int deltaX, int deltaY) {
    double dx = deltaX * zoom;
    double dy = deltaY * zoom;
    
    List<ISelectable> sx = model.getSelectedItems();
    if (!sx.isEmpty()) {
      for (ISelectable s : sx) {
        s.move(dx, dy);
      }
      redraw();
    }
  }

  
  private void deselectAllLines () {
    boolean itemsDeselected = model.deselectAllLines();
    if (itemsDeselected) {
      redraw();
    }
  }


}
