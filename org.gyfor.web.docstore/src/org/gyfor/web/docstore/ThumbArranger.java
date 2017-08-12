package org.gyfor.web.docstore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class ThumbArranger {

  private static final String NL = System.getProperty("line.separator");
  
  private static final int betweenGroupWidth = 0; //10;
  
  private static final int rightLimit = 750;
  
  
  private ThumbGroup[] thumbGroups = new ThumbGroup[0];
  
  
  public void addThumbGroup(ThumbGroup thumbGroup) {
    int n = thumbGroups.length;
    thumbGroups = Arrays.copyOf(thumbGroups, n + 1);
    thumbGroups[n] = thumbGroup;
  }
  
  
//  public int arrange (int rightLimit) {
//    int currentTop = 0;
//    int[] currentLeft = new int[1];
//    int[] rowHeight = new int[1];
//    
//    int i = 0;
//    while (i < thumbGroups.length) {
//      boolean clearTopBottom = false;
//      
//      // See if this group of thumbs will fit
//      int linearWidth = thumbGroups[i].getLinearWidth();
//      if (currentLeft[0] + linearWidth > rightLimit) {
//        // This group of thumbs will not fit
//        currentTop += rowHeight[0];
//        currentLeft[0] = 0;
//        rowHeight[0] = 0;
//        
//        if (linearWidth > rightLimit) {
//          clearTopBottom = false;
//        }
//      }
//      
//      // Arrange group thumbs
//      thumbGroups[i].arrange(rightLimit, currentLeft, rowHeight);
//      
//      currentLeft[0] += betweenGroupWidth;
//      if (clearTopBottom) {
//        currentTop += rowHeight[0];
//        currentLeft[0] = 0;
//        rowHeight[0] = 0;
//      }
//      i++;
//    }
//    if (currentLeft[0] > 0) {
//      currentTop += rowHeight[0];
//    }
//    return currentTop;
//  }
  
  
  private void validate() {
    // Verify out thumb groups
    LocalDate lastDate = LocalDate.MIN;
    for (ThumbGroup group : thumbGroups) {
      if (lastDate.compareTo(group.date) > 0) {
        throw new RuntimeException("Date error");
      }
      lastDate = group.date;
    }
  }

  
  public void buildListFromDirectory (String dirName) throws FileNotFoundException {
    File dir = new File(dirName);
    File[] files = dir.listFiles();
    
    Thumb[] thumbs = new Thumb[files.length];
    int j = 0;
    for (int i = 0; i < files.length; i++) {
      if (files[i].getName().toLowerCase().endsWith(".jpg")) {
        thumbs[j++] = new Thumb(dir, files[i].getName());
      }
    }
    thumbs = Arrays.copyOf(thumbs, j);
    
    Arrays.sort(thumbs, new Comparator<Thumb>() {

      @Override
      public int compare(Thumb arg0, Thumb arg1) {
        return arg0.date.compareTo(arg1.date);
      }
      
    });
    
    int i = 0;
    while (i < thumbs.length) {
      LocalDate thumbDate = thumbs[i].date;
      ThumbGroup thumbGroup = new ThumbGroup(thumbDate);
      
      while (i < thumbs.length && thumbDate.equals(thumbs[i].date)) {
        if (thumbs[i].path.toLowerCase().endsWith(".jpg")) {
          thumbGroup.addThumb(thumbs[i]);
        }
        i++;
      }
      addThumbGroup(thumbGroup);
    }
    validate();
    //int pageHeight = arrange(rightLimit);
    validate();
    
    ////MarkedScrollbar scrollbar = new MarkedScrollbar();
    ////scrollbar.build(thumbGroups);
    //MarkedScrollbar scrollbar = new MarkedScrollbar();
    //Marker parent = new Marker(0);
    //int height = 10000;
    //scrollbar.build(thumbGroups, parent, 0, height);
    //String x = scrollbar.toHTML(parent);
    
    // Dump out HTML for this list of thumb groups
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");
    
    String x = "";
    x += "<html>" + NL;
    x += "<head>" + NL;
    x += "<link href=\"index.css\" rel=\"stylesheet\">" + NL;
    x += "</head>" + NL;
    x += "<body>" + NL;
    //x += scrollbar.toHTML(pageHeight) + NL;
    x += "<div id='thumbGroupSet'>" + NL;
    for (ThumbGroup thumbGroup : thumbGroups) {
      x += thumbGroup.toHTML();
    }
    x += "</div>" + NL;
    x += "</body>" + NL;
    x += "</html>";
    PrintWriter writer = new PrintWriter(new File(dirName, "index.html"));
    writer.print(x);
    writer.close();

  }
  
  
  public static void main(String[] args) throws Exception {
    ThumbArranger arranger = new ThumbArranger();
    arranger.buildListFromDirectory("/Users/Kevin/Pictures/Andre");
  }
}
