package org.leadlightdesigner;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import org.leadlightdesigner.design.DPoint;

public class ImageDeskewer {

  
  private BufferedImage rotateImage(BufferedImage img, double angle) {
    double sin = Math.abs(Math.sin(angle));
    double cos = Math.abs(Math.cos(angle));
    int w = img.getWidth();
    int h = img.getHeight();
    int newWidth = (int) Math.floor(w * cos + h * sin);
    int newHeight = (int) Math.floor(h * cos + w * sin);

    BufferedImage img2 = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = img2.createGraphics();
    AffineTransform at = new AffineTransform();
    at.translate((newWidth - w) / 2, (newHeight - h) / 2);

    int x = w / 2;
    int y = h / 2;

    at.rotate(angle, x, y);

    g2d.drawImage(img, at, null);
    g2d.dispose();

    
    return img2;
  }
  
  
  public void deskew (String fileName) {
    try {
      BufferedImage img = ImageIO.read(new File("17 Burwood Ave.jpg"));
      //img.setCorners(612, 783, 2262, 506, 2298, 1467, 524, 1597);
      DPoint[] corners = new DPoint[] {
          new DPoint(612, 783),
          new DPoint(2262, 506),
          new DPoint(2298, 1467),
          new DPoint(524, 1597),
      };
      
      double topLineAngle = corners[0].getVectorAngle(corners[1]);
      
      // Window dimension 840mm x 465mm
      BufferedImage img2 = rotateImage(img, -topLineAngle);
      ImageIO.write(img2, "PNG", new File("17 Burwood Ave.png"));
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
   
  
  }
  
  
  public static void main (String[] args) {
    ImageDeskewer ds = new ImageDeskewer();
    ds.deskew("17 Burwood Ave.jpg");
  }
}

