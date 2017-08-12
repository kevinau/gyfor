package org.gyfor.web.docstore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ThumbGroup implements IMarkable {

  private static final String NL = System.getProperty("line.separator");
  
  private final LocalDate date;
  
  private final LabelProducer labelProducer;
  
  private final int[] levels;
  
  Thumb[] thumbs = new Thumb[0];
  
  
  ThumbGroup (LocalDate date, LabelProducer labelProducer) {
    this.date = date;
    this.labelProducer = labelProducer;
    this.levels = new int[] {
        date.getYear(),
        date.getMonthValue(),
        date.getDayOfMonth(),
    };
  }
  
  
  void addThumb(Thumb thumb) {
    if (!thumb.getDate().equals(date)) {
      throw new IllegalArgumentException("Thumb does not have the same date as this thumbGroup");
    }
    int n = thumbs.length;
    thumbs = Arrays.copyOf(thumbs, n + 1);
    thumbs[n] = thumb;
  }
  
  
//  int getLinearWidth() {
//    int width = withinGroupWidth * (thumbs.length - 1);
//    for (Thumb thumb : thumbs) {
//      width += thumb.width;     
//    }
//    return width;
//  }
  
 
//  void arrange(int rightLimit, int[] left, int[] rowHeight) {
//    int i = 0;
//    while (i < thumbs.length) {
//      if (left[0] + thumbs[i].width > rightLimit) {
//        // This thumb will not fit, so start a new line
//        rowHeight[0] = 0;
//        left[0] = 0;
//      }
//      thumbs[i].left = left[0];
//      left[0] += thumbs[i].width + withinGroupWidth;
//      rowHeight[0] = max(rowHeight[0], thumbs[i].height);
//      i++;
//    }
//  }


  public int getLevel(int depth) {
    return levels[depth];
  }

  
  private static final DateTimeFormatter formatterx = DateTimeFormatter.ofPattern("yyyyMMdd");

  
  @Override
  public String getLabel(int level) {
    return labelProducer.getLabel(level, date);
  }


  @Override
  public String getTargetId() {
    return "thumbGroup" + formatterx.format(date);
  }
  
  
  public String getDateLabel() {
    return labelProducer.getLabel(date);
  }
  
  
  public Thumb[] getThumbs() {
    return thumbs;
  }
  
  
  public String getLevelsx() {
    return levels[0] + "|" + levels[1] + "|" + levels[2];
  }

  
  public String getLabelsx() {
    return getLabel(0) + "|" + getLabel(1) + "|" + getLabel(2);
  }

  
  public String toHTML() {
    String levels3 = levels[0] + "|" + levels[1] + "|" + levels[2];
    String labels3 = getLabel(0) + "|" + getLabel(1) + "|" + getLabel(2);
    String x = "";
    x += "<div id='" + getTargetId() + "' class='thumbGroup' data-levels='" + levels3 + "' data-labels='" + labels3 + "'>" + NL;
    x += "<div class='date'>" + dateFormatter.format(date) + "</div>" + NL;
    x += "<div class='thumbs'>";
    for (Thumb thumb : thumbs) {
      x += thumb.toHTML();
    }
    x += "</div>" + NL;
    x += "</div>" + NL;
    return x;
  }
}
