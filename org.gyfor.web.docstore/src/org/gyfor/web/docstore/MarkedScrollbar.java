package org.gyfor.web.docstore;

import java.util.ArrayList;
import java.util.List;

public class MarkedScrollbar {

  private static final String NL = System.getProperty("line.separator");
  
  
  private static final int labelHeight = 12;

  private static final int VIEWPORT_HEIGHT = 500;
  
  
  public static class Marker {
    int offset;
    String label;
    int level;
    String targetId;
    
    
    public Marker(int offset, String label, int level, String targetId) {
      this.offset = offset;
      this.label = label;
      this.level = level;
      this.targetId = targetId;
    }
    
    
    public Marker(int offset) {
      this.offset = offset;
      this.label = null;
    }
    
    
    public String toHTML(int level, int viewportHeight, int pageHeight) {
      String x = "";
      //int scaledOffset = (offset * viewportHeight) / pageHeight;
      double scaledOffset = ((offset + 30) * 100.0) / (pageHeight + 30);
      x += "<div style='top:" + scaledOffset + "%'>";
      x += "<a class='marker" + level + "' href='#" + targetId + "'>" + label + "</a>";
      x += "</div>" + NL;
      return x;
    }
    
  }
  
  
  private List<Marker> markers = new ArrayList<>();
  
  
//  private List<Marker> subsetsByLevel(IMarkable[] items, int level) {
//    List<Marker> subsets = new ArrayList<>();
//    
//    int i = 0;
//    System.out.println("subset by level " + i);
//    while (i < items.length) {
//      System.out.println("subset by level " + i);
//      String label = items[i].getLabel(level);
//      IMarkable[] subset = new IMarkable[0];
//      while (i < items.length && label.equals(items[i].getLabel(level))) {
//        int m = subset.length;
//        subset = Arrays.copyOf(subset, m + 1);
//        subset[m] = items[i];
//        i++;
//      }
//      subsets.add(new Marker(subset, level));
//    }
//    System.out.println("subset by level = " + subsets.size());
//
//    return subsets;
//  }
  
  
  public void build(ThumbGroup[] thumbGroups) {   
//    Arrays.sort(items, new Comparator<IMarkable>() {
//      @Override
//      public int compare(IMarkable arg0, IMarkable arg1) {
//        return arg0.getOffset() - arg1.getOffset();
//      }
//    });

    int[] priorLevels = new int[3];
    
    int i = 0;
    while (i < thumbGroups.length) {
      int j = 0;
      while (j < priorLevels.length) {
        if (thumbGroups[i].getLevel(j) != priorLevels[j]) {
          break;
        }
        j++;
      }
      
      if (j < priorLevels.length) {
        Marker marker = new Marker(offset, thumbGroups[i].getLabel(j), j, thumbGroups[i].getTargetId());
        markers.add(marker);
      }
      
      for (j = 0; j < priorLevels.length; j++) {
        priorLevels[j] = thumbGroups[i].getLevel(j);
      }
      
      int minOffset = offset + labelHeight;
      while (i < thumbGroups.length && thumbGroups[i].getOffset() < minOffset) {
        i++;
      }
    }
  }
  
  
  public String toHTML(int pageHeight) {
    String x = "";
    x += "<div id='markers'>" + NL;
    for (Marker marker : markers) {
      x += marker.toHTML(0, VIEWPORT_HEIGHT, pageHeight);
    }
    x += "</div>" + NL;
    return x;
  }
  
}
