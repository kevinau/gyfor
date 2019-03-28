package org.leadlightdesigner.files;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.leadlightdesigner.AttributeHandler;
import org.leadlightdesigner.guide.GuideAttributes;

public class NavigatorModel {

  private static final String[] imageSuffixes = ImageIO.getReaderFileSuffixes();
  
  private final Path rootDir;
  
  private List<NavigatorItem> items;
  
  private List<INavigatorEvent> changeEvents = new ArrayList<>();
  
  
  private static boolean isImageFile (Path path) {
    String fileName = path.getFileName().toString().toLowerCase();
    for (String suffix : imageSuffixes) {
      if (fileName.endsWith(suffix)) {
        return true;
      }
    }
    return false;
  }

  
  public void addChangeEvent (INavigatorEvent x) {
    changeEvents.add(x);
  }
  
  
  public void removeChangeEvent (INavigatorEvent x) {
    changeEvents.remove(x);
  }
  
  
  protected void fireItemAddedEvents (NavigatorItem parentItem, NavigatorItem item) {
    for (INavigatorEvent ev : changeEvents) {
      ev.itemAdded(parentItem, item);
    }
  }
  
  
  private void loadTreeItem (Path path, List<NavigatorItem> items) {
    Path resolvedPath = rootDir.resolve(path);

    if (Files.isDirectory(resolvedPath)) {
      List<NavigatorItem> subItems = new ArrayList<>();
      loadSubtree (path, subItems);
      items.add(new NavigatorFolder(this, rootDir, path, subItems));
    } else {
      if (isImageFile(path)) {
        try {
          // Is this a user image file, an extracted guide image, or a composite final image
          UserDefinedFileAttributeView view = Files.getFileAttributeView(resolvedPath, UserDefinedFileAttributeView.class);
          List<String> names = view.list();
          if (names.contains("design.guide")) {
            GuideAttributes attrib = AttributeHandler.getAttributes(resolvedPath, "design.guide", GuideAttributes.class);
            items.add(new NavigatorGuide(this, rootDir, path, attrib));
          } else if (names.contains("design.final")) {
            FinalAttributes attrib = AttributeHandler.getAttributes(resolvedPath, "design.final", FinalAttributes.class);
            items.add(new NavigatorFinal(this, rootDir, path, attrib));
          } else {
            items.add(new NavigatorImage(this, rootDir, path));
          }
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      } else {
        items.add(new NavigatorFile(this, rootDir, path));
      }
    }
  }
  
  
  private void loadSubtree (Path dir, List<NavigatorItem> items) {
    Path resolvedDir = rootDir.resolve(dir);
    
    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(resolvedDir)) {
      for (Path path : dirStream) {
        Path relativePath = rootDir.relativize(path);
        loadTreeItem (relativePath, items);
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
  
  
  public NavigatorModel (Path rootDir) {
    this.rootDir = rootDir;
    refresh ();
  }
  
  
  public List<NavigatorItem> getItems () {
    return items;
  }
  
  
  public void refresh () {
    items = new ArrayList<>();
    
    // Find directories and files
    File[] files = rootDir.toFile().listFiles();
    for (File file : files) {
      Path relativePath = rootDir.relativize(file.toPath());
      loadTreeItem (relativePath, items);
    }
    
    for (NavigatorItem item : items) {
      item.dump(0);
    }

    // Add links
    walkItems (i -> {
      i.linkSource(this);
    });
    
  }
  
  
  public void dump () {
    System.out.println("Navigator model:");
    for (NavigatorItem item : items) {
      item.dump(1);
    }    
  }
  
  
  public void walkItems (Consumer<NavigatorItem> consumer) {
    for (NavigatorItem item : items) {
      item.walkItems(consumer);
    }
  }

}
