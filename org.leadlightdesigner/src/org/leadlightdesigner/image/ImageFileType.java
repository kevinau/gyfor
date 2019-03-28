package org.leadlightdesigner.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.leadlightdesigner.CachedImage;
import org.leadlightdesigner.IManagedFileType;

public class ImageFileType implements IManagedFileType {

  private static final String[] imageSuffixes = ImageIO.getReaderFileSuffixes();

  @Override
  public Image getFileIcon(Widget widget) {
    return CachedImage.getImage(widget, "icons/obj16/camera.png");
  }

  @Override
  public String getManagedFileName() {
    return "Image";
  }

  @Override
  public String getFilterExtensions() {
    String imageGlobs = "*" + imageSuffixes[0];
    for (int i = 1; i < imageSuffixes.length; i++) {
      imageGlobs += ";*" + imageSuffixes[i];
    }
    return imageGlobs;
  }

  @Override
  public String getFilterNames() {
    return "Image files";
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T load(File file, Class<T> objClass) {
    BufferedImage bufferedImage;
    try {
      bufferedImage = ImageIO.read(file);
      if (bufferedImage == null) {
        throw new RuntimeException("No support for image file type");
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    return (T)bufferedImage;
  }

}
