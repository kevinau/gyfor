package org.plcore.docstore.parser.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objectplanet.image.PngEncoder;

public class ImageIO {

  private final static Logger logger = LoggerFactory.getLogger(ImageIO.class);
  
  private final static int GAP = 16;
  private final static Color GAP_COLOR = new Color(183, 183, 183);
  private final static int THUMBNAIL_SIZE = 200;
  
  
  public static BufferedImage appendImage (BufferedImage singleImage, BufferedImage addition) {
    if (singleImage == null) {
      return addition;
    } else {
      // Append the image to the single image
      int h0 = singleImage.getHeight();
      int h = h0 + GAP + addition.getHeight();
      int w = Integer.max(singleImage.getWidth(), addition.getWidth());
      BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = newImage.createGraphics();
      
      g2.drawImage(singleImage, null, 0, 0);

      if (GAP > 0) {
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(GAP_COLOR);
        g2.fillRect(0, h0, w, GAP);
        //draw gap
        g2.setColor(oldColor);
      }
      
      g2.drawImage(addition, null, 0, singleImage.getHeight() + GAP);
      g2.dispose();
      return newImage;
    }
  }
    
  
  public static BufferedImage overlayImage (BufferedImage baseImage, BufferedImage addition, int x, int y) {
    // Overlay an image on top of a base image    
    if (x != 0 || y != 0) {
      logger.info("Overlaying a image at ({},{})", x, y);
    }
    int x1 = x + addition.getWidth();
    int y1 = y + addition.getHeight();
    int w = Integer.max(baseImage.getWidth(), x1);
    int h = Integer.max(baseImage.getHeight(), y1);
    BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    
    Graphics2D g2 = newImage.createGraphics();
    g2.drawImage(baseImage, null, 0, 0);
    g2.drawImage(addition, null, x, y);
    g2.dispose();
    return newImage;
  }
    
  
  public static BufferedImage resizeImage (BufferedImage singleImage, int newWidth, double[] factorOut) {
    int oldWidth = singleImage.getWidth();
    double ratio = ((double)singleImage.getHeight()) / oldWidth;
    int newHeight = (int)(newWidth * ratio + 0.99);                
    BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.OPAQUE);
    Graphics2D g2 = newImage.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2.drawImage(singleImage, 0, 0, newWidth, newHeight, null);
    g2.dispose();
    if (factorOut != null) {
      factorOut[0] = ((double)newWidth) / oldWidth;
    }
    return newImage;
  }
  

  public static BufferedImage getImage (Path path) {
    try {
      return javax.imageio.ImageIO.read(path.toFile());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public static void writeThumbnail (BufferedImage image, Path thumbsFile) {
    if (image.getHeight() > THUMBNAIL_SIZE && image.getWidth() > THUMBNAIL_SIZE) {
      image = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }
    writeImage (image, thumbsFile);
  }
  

  public static void writeImage (BufferedImage image, Path imageFile) {
    PngEncoder pngEncoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR);
    try (FileOutputStream imageOut = new FileOutputStream(imageFile.toFile())) {
      pngEncoder.encode(image, imageOut);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public static void writeImage (BufferedImage image, OutputStream outputStream) throws IOException {
    PngEncoder pngEncoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR);
    pngEncoder.encode(image, outputStream);
  }
}
