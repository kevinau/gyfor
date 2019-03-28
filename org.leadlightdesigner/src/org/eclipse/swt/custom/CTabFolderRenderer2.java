package org.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class CTabFolderRenderer2 extends CTabFolderRenderer {

  private static final int PADDING = 20;
  
  public CTabFolderRenderer2(CTabFolder parent) {
    super(parent);
  }

  
  @Override
  void drawSelected(int itemIndex, GC gc, Rectangle bounds, int state ) {
    CTabItem item = parent.items[itemIndex];
    int x = bounds.x;
    int y = bounds.y;
    int height = bounds.height;
    int width = bounds.width;
    if (!parent.simple && !parent.single) width -= (curveWidth - curveIndent);
    width += 6;
    int borderLeft = parent.borderVisible ? 1 : 0;
    int borderRight = borderLeft;
    int borderTop = parent.onBottom ? borderLeft : 0;
    int borderBottom = parent.onBottom ? 0 : borderLeft;

    Point size = parent.getSize();

    int rightEdge = Math.min (x + width, parent.getRightItemEdge(gc));
    //   Draw selection border across all tabs

    if ((state & SWT.BACKGROUND) != 0) {
      int highlight_header = (parent.getStyle() & SWT.FLAT) != 0 ? 1 : 3;
      int xx = borderLeft;
      int yy = parent.onBottom ? size.y - borderBottom - parent.tabHeight - highlight_header : borderTop + parent.tabHeight + 1;
      int ww = size.x - borderLeft - borderRight;
      int hh = highlight_header - 1;
      int[] shape = new int[] {xx,yy, xx+ww,yy, xx+ww,yy+hh, xx,yy+hh};
      if (parent.selectionGradientColors != null && !parent.selectionGradientVertical) {
        drawBackground(gc, shape, parent.shouldHighlight());
      } else {
        gc.setBackground(parent.shouldHighlight() ? parent.selectionBackground : parent.getBackground());
        gc.fillRectangle(xx, yy, ww, hh);
      }

      if (parent.single) {
        if (!item.showing) return;
      } else {
        // if selected tab scrolled out of view or partially out of view
        // just draw bottom line
        if (!item.showing){
          int x1 = Math.max(0, borderLeft - 1);
          int y1 = (parent.onBottom) ? y - 1 : y + height;
          int x2 = size.x - borderRight;
          gc.setForeground(parent.getDisplay().getSystemColor(BORDER1_COLOR));
          gc.drawLine(x1, y1, x2, y1);
          return;
        }

        // draw selected tab background and outline
        shape = null;
        if (parent.onBottom) {
          int[] left = parent.simple ? SIMPLE_BOTTOM_LEFT_CORNER : BOTTOM_LEFT_CORNER;
          int[] right = parent.simple ? SIMPLE_BOTTOM_RIGHT_CORNER : curve;
          if (borderLeft == 0 && itemIndex == parent.firstIndex) {
            left = new int[]{x, y+height};
          }
          shape = new int[left.length+right.length+8];
          int index = 0;
          shape[index++] = x; // first point repeated here because below we reuse shape to draw outline
          shape[index++] = y - 1;
          shape[index++] = x;
          shape[index++] = y - 1;
          for (int i = 0; i < left.length/2; i++) {
            shape[index++] = x + left[2*i];
            shape[index++] = y + height + left[2*i+1] - 1;
          }
          for (int i = 0; i < right.length/2; i++) {
            shape[index++] = parent.simple ? rightEdge - 1 + right[2*i] : rightEdge - curveIndent + right[2*i];
            shape[index++] = parent.simple ? y + height + right[2*i+1] - 1 : y + right[2*i+1] - 2;
          }
          shape[index++] = parent.simple ? rightEdge - 1 : rightEdge + curveWidth - curveIndent;
          shape[index++] = y - 1;
          shape[index++] = parent.simple ? rightEdge - 1 : rightEdge + curveWidth - curveIndent;
          shape[index++] = y - 1;
        } else {
          int[] left = parent.simple ? SIMPLE_TOP_LEFT_CORNER : TOP_LEFT_CORNER;
          int[] right = parent.simple ? SIMPLE_TOP_RIGHT_CORNER : curve;
          if (borderLeft == 0 && itemIndex == parent.firstIndex) {
            left = new int[]{x, y};
          }
          shape = new int[left.length+right.length+8];
          int index = 0;
          shape[index++] = x; // first point repeated here because below we reuse shape to draw outline
          shape[index++] = y + height + 1;
          shape[index++] = x;
          shape[index++] = y + height + 1;
          for (int i = 0; i < left.length/2; i++) {
            shape[index++] = x + left[2*i];
            shape[index++] = y + left[2*i+1];
          }
          for (int i = 0; i < right.length/2; i++) {
            shape[index++] = parent.simple ? rightEdge - 1 + right[2*i] : rightEdge - curveIndent + right[2*i];
            shape[index++] = y + right[2*i+1];
          }
          shape[index++] = parent.simple ? rightEdge - 1 : rightEdge + curveWidth - curveIndent;
          shape[index++] = y + height + 1;
          shape[index++] = parent.simple ? rightEdge - 1 : rightEdge + curveWidth - curveIndent;
          shape[index++] = y + height + 1;
        }

        Rectangle clipping = gc.getClipping();
        Rectangle clipBounds = item.getBounds();
        clipBounds.height += 1;
        if (parent.onBottom) clipBounds.y -= 1;
        boolean tabInPaint = clipping.intersects(clipBounds);

        if (tabInPaint) {
          // fill in tab background
          if (parent.selectionGradientColors != null && !parent.selectionGradientVertical) {
            drawBackground(gc, shape, true);
          } else {
            Color defaultBackground = parent.shouldHighlight() ? parent.selectionBackground : parent.getBackground();
            Image image = parent.selectionBgImage;
            Color[] colors = parent.selectionGradientColors;
            int[] percents = parent.selectionGradientPercents;
            boolean vertical = parent.selectionGradientVertical;
            xx = x;
            yy = parent.onBottom ? y -1 : y + 1;
            ww = width;
            hh = height;
            if (!parent.single && !parent.simple) ww += curveWidth - curveIndent;
            drawBackground(gc, shape, xx, yy, ww, hh, defaultBackground, image, colors, percents, vertical);
          }
        }

        //Highlight MUST be drawn before the outline so that outline can cover it in the right spots (start of swoop)
        //otherwise the curve looks jagged
        drawHighlight(gc, bounds, state, rightEdge);

        // draw outline
        shape[0] = Math.max(0, borderLeft - 1);
        if (borderLeft == 0 && itemIndex == parent.firstIndex) {
          shape[1] = parent.onBottom ? y + height - 1 : y;
          shape[5] = shape[3] = shape[1];
        }
        shape[shape.length - 2] = size.x - borderRight + 1;
        for (int i = 0; i < shape.length/2; i++) {
          if (shape[2*i + 1] == y + height + 1) shape[2*i + 1] -= 1;
        }
        Color borderColor = parent.getDisplay().getSystemColor(BORDER1_COLOR);
        if (! borderColor.equals(lastBorderColor)) createAntialiasColors();
        antialias(shape, selectedInnerColor, selectedOuterColor, gc);
        gc.setForeground(borderColor);
        gc.drawPolyline(shape);

        if (!tabInPaint) return;
      }
    }

    if ((state & SWT.FOREGROUND) != 0) {
      // draw Image
      Rectangle trim = computeTrim(itemIndex, SWT.NONE, 0, 0, 0, 0);
      int xDraw = x - trim.x;
      if (parent.single && (parent.showClose || item.showClose)) xDraw += item.closeRect.width;
      Image image = item.getImage();
      if (image != null && !image.isDisposed()) {
        Rectangle imageBounds = image.getBounds();
        // only draw image if it won't overlap with close button
        int maxImageWidth = rightEdge - xDraw - (trim.width + trim.x);
        if (!parent.single && item.closeRect.width > 0) maxImageWidth -= item.closeRect.width + INTERNAL_SPACING;
        if (imageBounds.width < maxImageWidth) {
          int imageX = xDraw;
          int imageY = y + (height - imageBounds.height) / 2;
          imageY += parent.onBottom ? -1 : 1;
          gc.drawImage(image, imageX, imageY);
          xDraw += imageBounds.width + INTERNAL_SPACING;
        }
      }

      // draw Text
      int textWidth = rightEdge - xDraw - (trim.width + trim.x);
      if (!parent.single && item.closeRect.width > 0) textWidth -= item.closeRect.width + INTERNAL_SPACING;
      if (textWidth > 0) {
        Font gcFont = gc.getFont();
        gc.setFont(item.font == null ? parent.getFont() : item.font);

        if (item.shortenedText == null || item.shortenedTextWidth != textWidth) {
          item.shortenedText = shortenText(gc, item.getText(), textWidth);
          item.shortenedTextWidth = textWidth;
        }
        Point extent = gc.textExtent(item.shortenedText, FLAGS);
        int textY = y + (height - extent.y) / 2;
        textY += parent.onBottom ? -1 : 1;

        gc.setForeground(parent.selectionForeground);
        gc.drawText(item.shortenedText, xDraw, textY, FLAGS);
        gc.setFont(gcFont);

        // draw a Focus rectangle
        if (parent.isFocusControl()) {
          Display display = parent.getDisplay();
          if (parent.simple || parent.single) {
            gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
            gc.drawFocus(xDraw-1, textY-1, extent.x+2, extent.y+2);
          } else {
            gc.setForeground(display.getSystemColor(BUTTON_BORDER));
            // The following line has been removed
            // gc.drawLine(xDraw, textY+extent.y+1, xDraw+extent.x+1, textY+extent.y+1);
          }
        }
      }
      if (parent.showClose || item.showClose) drawClose(gc, item.closeRect, item.closeImageState);
    }
  }


}
