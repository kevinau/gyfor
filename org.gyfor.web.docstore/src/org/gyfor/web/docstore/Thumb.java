package org.gyfor.web.docstore;

import java.time.LocalDate;

public class Thumb {

  private static final int THUMB_VIEW_HEIGHT = 120;
  
  private static final int IMAGE_WIDTH = 3;
  private static final int IMAGE_HEIGHT = 1;
  private static final int DATE_TAKEN = 36867;
  
  private final String hashcode;
  
  private LocalDate date;
  
  private int width;
  
  private int height;
  

  public Thumb (String hashcode, LocalDate date) {
    this.hashcode = hashcode;
    this.date = date;
  }
  
  
//  public Thumb (File baseDir, String relativePath) {
//    File file = new File(baseDir, relativePath);
//    this.path = relativePath;
//    
//    // Assuming the file is an image, get its size
////    try (ImageInputStream in = ImageIO.createImageInputStream(file)) {
////      final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
////      if (readers.hasNext()) {
////        ImageReader reader = readers.next();
////        try {
////          reader.setInput(in);
////          this.width = reader.getWidth(0);
////          this.height = reader.getHeight(0);
////        } finally {
////          reader.dispose();
////        }
////      } else {
////        throw new RuntimeException("Cannot determine image width/height for: " + file);
////      }
////    } catch (IOException ex) {
////      throw new RuntimeException(ex);
////    } 
//    
//    // Get image metadata
//    try {
//      Metadata metadata = ImageMetadataReader.readMetadata(file);
//      for (Directory directory : metadata.getDirectories()) {
//        for (Tag tag : directory.getTags()) {
//          String x;
//          int n;
//          
//          switch (tag.getTagType()) {
//          case DATE_TAKEN :
//            if (tag.getDirectoryName().equals("Exif SubIFD")) {
//              x = tag.getDescription();
//              n = x.indexOf(' ');
//              x = x.substring(0, n).replace(':', '-');
//              date = LocalDate.parse(x);
//            }
//            break;
//          case IMAGE_WIDTH :
//            if (tag.getDirectoryName().equals("JPEG")) {
//              x = tag.getDescription();
//              n = x.indexOf(' ');
//              x = x.substring(0, n);
//              width = Integer.parseInt(x);
//            }
//            break;
//          case IMAGE_HEIGHT :
//            if (tag.getDirectoryName().equals("JPEG")) {
//              x = tag.getDescription();
//              n = x.indexOf(' ');
//              x = x.substring(0, n);
//              height = Integer.parseInt(x);
//            }
//            break;
//          }
//        }
//      }
//    } catch (ImageProcessingException | IOException ex) {
//      throw new RuntimeException(ex);
//    }
//   
//    width = (THUMB_VIEW_HEIGHT * width) / height;
//    height = THUMB_VIEW_HEIGHT;
//  }
  
  
  public String getHashcode() {
    return hashcode;
  }
  
  
  public LocalDate getDate() {
    return date;
  }
  
  
  public String toHTML() {
    String x = "";
    x += "<img src='" + hashcode + "'>";
    return x;
  }
  
  
//  private void displayMetadata(Node node) {
//    System.out.println(node.getLocalName() + ": " + node.getAttributes());
//    NodeList childNodes = node.getChildNodes();
//    for (int i = 0; i < childNodes.getLength(); i++) {
//      displayMetadata(childNodes.item(i));
//    }
//  }
}
