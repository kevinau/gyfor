package org.leadlightdesigner.guide;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.leadlightdesigner.AttributeHandler;
import org.leadlightdesigner.FileTabItem;
import org.leadlightdesigner.GuidePoint;


public class GuideTabItem extends FileTabItem {

  private final Shell shell;
  private final Path sourcePath;
  private final Cursor exitCursor;
  
  private int targetWidth = 841;
  
  private int targetHeight = 465;
  
  private List<GuidePoint> points = new ArrayList<>();
  
  private double zoom = 1.0;
  
  private boolean overPoint = false;
  
  
  public GuideTabItem(CTabFolder parent, Path rootDir, Path sourcePath, Path guidePath) {
    super(parent, rootDir, guidePath, new GuideManagedFileType());
    
    shell = parent.getShell();
    exitCursor = parent.getCursor();


//    points.add(new GuidePoint(20, 20));
//    points.add(new GuidePoint(50, 100));
//    points.add(new GuidePoint(100, 200));
//    points.add(new GuidePoint(200, 400));
    //new Point2D_F64(612, 783), 
    //new Point2D_F64(2214, 516),
    //new Point2D_F64(2244, 1467), 
    //new Point2D_F64(524, 1597),

    this.sourcePath = sourcePath;
    
    super.setText(guidePath.getFileName().toString());
    super.setToolTipText(guidePath.toString());

    Image image = new Image (parent.getDisplay(), rootDir.resolve(sourcePath).toString());
    double proportions = (double)(image.getBounds().height) / (double)(image.getBounds().width); 

    ScrolledComposite scomposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    Canvas canvas = new Canvas(scomposite, SWT.DOUBLE_BUFFERED);
    
    Display display = parent.getDisplay();
    Rectangle displaySize = display.getClientArea();
    canvas.setSize(displaySize.width / 2, (int)(displaySize.width * proportions / 2));
    zoom = ((double)image.getBounds().width) / (displaySize.width / 2.0);
    Image resizedImage = resize(image, displaySize.width / 2, (int)(displaySize.width * proportions / 2));
    
    canvas.addListener (SWT.Paint, new Listener () {
        @Override
        public void handleEvent (Event ev) {
            GC gc = ev.gc;
            gc.drawImage (resizedImage, 0, 0);
            if (points.size() == 4) {
              drawConnectingLines(display, gc);
            }
            for (GuidePoint point : points) {
              point.drawOverImage(display, gc, zoom);
            }
            gc.dispose();
        }
    });
    
    canvas.addMouseTrackListener(new MouseTrackListener() {

      @Override
      public void mouseEnter(MouseEvent arg0) {
        Cursor cursor = getDisplay().getSystemCursor(SWT.CURSOR_CROSS);
        shell.setCursor(cursor);
      }

      @Override
      public void mouseExit(MouseEvent arg0) {
        shell.setCursor(exitCursor);
      }

      @Override
      public void mouseHover(MouseEvent arg0) {
        // TODO Auto-generated method stub
      }
      
    });
    
    canvas.addMouseListener(new MouseListener() {
      @Override
      public void mouseDoubleClick(MouseEvent ev) {
        // TODO Auto-generated method stub
      }

      @Override
      public void mouseDown(MouseEvent ev) {
        if (ev.button == 1) {
          GuidePoint over = getOverPoint(ev);
          if (over == null) {
            if (points.size() < 4) {
              GuidePoint newPoint = new GuidePoint(ev.x, ev.y, zoom);
              points.add(newPoint);
              setModified(true);
              setSelected(newPoint);
            } else {
              clearSelected();
            }
          } else {
            if (over.isSelected()) {
              over.setSelected(false);
            } else {
              setSelected(over);
            }
          }
          canvas.redraw();
        }
      }

      @Override
      public void mouseUp(MouseEvent ev) {
      }
      
    });
     
    canvas.addMouseMoveListener(new MouseMoveListener() {
      @Override
      public void mouseMove(MouseEvent ev) {
        GuidePoint over = getOverPoint(ev);
        if (over == null) {
          setOverPoint(false);
        } else {
          setOverPoint(true);
        }
      }
    });
     
    canvas.addKeyListener(new KeyListener() {

      @Override
      public void keyPressed(KeyEvent ev) {
        if (ev.keyCode == 127) {
          for (GuidePoint p : points) {
            if (p.isSelected()) {
              points.remove(p);
              setModified(true);
              canvas.redraw();
              break;
            }
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent ev) {
      }
      
    });
    scomposite.setContent(canvas);

    //composite.setLayout(new FillLayout());
    //Label label = new Label (composite, SWT.NONE);
    //label.setImage (image);
    super.setControl(scomposite);
  }
  
  
  private static Image resize(Image image, int width, int height) {
    Image scaled = new Image(Display.getDefault(), width, height);
    GC gc = new GC(scaled);
    gc.setAntialias(SWT.ON);
    gc.setInterpolation(SWT.HIGH);
    gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
    gc.dispose();
    image.dispose(); // don't forget about me!
    return scaled;
  }
  
  
  Path getSourcePath () {
    return sourcePath;
  }
  
  
  int getTargetWidth () {
    return targetWidth;
  }
  
  
  int getTargetHeight () {
    return targetHeight;
  }
  
  
  List<GuidePoint> getPoints () {
    return points;
  }
  
  
  private void drawConnectingLines(Display display, GC gc) {
    GuidePoint[] sorted = GuidePoints.sortByAngle(points);
    
    gc.setLineWidth(4);
    gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
    
    GuidePoint p0 = sorted[0];
    GuidePoint px = p0;
    for (int i = 1; i < 4; i++) {
      GuidePoint p = sorted[i];
      gc.drawLine(px.getX(zoom), px.getY(zoom), p.getX(zoom), p.getY(zoom));
      px = p;
    }
    gc.drawLine(px.getX(zoom), px.getY(zoom), p0.getX(zoom), p0.getY(zoom));
  }

  
  private void setSelected (GuidePoint point) {
    for (GuidePoint p : points) {
      if (p.isSelected()) {
        if (p == point) {
          point.toggleSelected();
        } else {
          p.setSelected(false);
          point.setSelected(true);
        }
        break;
      }
    }
    point.setSelected(true);
  }


  private void clearSelected () {
    for (GuidePoint p : points) {
      if (p.isSelected()) {
        p.setSelected(false);
        break;
      }
    }
  }


  private GuidePoint getOverPoint (MouseEvent arg0) {
    for (GuidePoint point : points) {
      if (point.isOver(arg0.x,  arg0.y, zoom)) {
        return point;
      }
    }
    return null;
  }

  
  private void setOverPoint (boolean overPoint) {
    if (this.overPoint != overPoint) {
      Cursor cursor;
      if (overPoint) {
        cursor = getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
      } else {
        cursor = getDisplay().getSystemCursor(SWT.CURSOR_CROSS);
      }
      shell.setCursor(cursor);
      this.overPoint = overPoint;
    }
  }
  
  
  @Override
  public void buildPopupMenu (final Menu menu) {
    super.buildPopupMenu(menu);
  }

  
  @Override
  protected boolean saveManagedFile(Path savePath) {
    try {
      System.out.println("Image deskewer: " + sourcePath.toAbsolutePath());
      File x = new File(".");
      System.out.println(x.getAbsolutePath());
      
      BufferedImage flat = ImageDeskewer.deskew(getRootDir().resolve(sourcePath), 841, 465, points);
      ImageIO.write(flat, "png", savePath.toFile());
    } catch (IOException ex) {
      ex.printStackTrace();
      // TODO display an error dialog
      return false;
    }

    GuideAttributes guideAttributes = new GuideAttributes(sourcePath, targetWidth, targetHeight, points);
    System.out.println(guideAttributes.getSource());
    System.out.println(guideAttributes.getTargetWidth());
    System.out.println(guideAttributes.getTargetHeight());
    System.out.println(guideAttributes.getCoords().length);
    
    AttributeHandler.saveAttributes(savePath, GuideAttributes.NAME, guideAttributes);
    return true;
  }

}
