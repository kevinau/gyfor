package org.gyfor.web.docstore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThumbGroupBuilder {

  private static final DateTimeFormatter[] formatters = {
      DateTimeFormatter.ofPattern("d MMM yyyy"),
      DateTimeFormatter.ofPattern("d MMM"),
      DateTimeFormatter.ofPattern("d"),
  };
  
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy");

  
  private LabelProducer labelProducer;
  
  
  public ThumbGroupBuilder () {
    labelProducer = new LabelProducer() {

      @Override
      public String getLabel(int level, LocalDate date) {
        return formatters[level].format(date);
      }

      @Override
      public String getLabel(LocalDate date) {
        return formatter.format(date);
      }
    };
  }

  
  public List<ThumbGroup> buildThumbGroups(Thumb[] thumbs) {
    List<ThumbGroup> resultGroups = new ArrayList<>();
    int i = 0;
    while (i < thumbs.length) {
      LocalDate documentDate = thumbs[i].getDate();
      ThumbGroup thumbGroup = new ThumbGroup(documentDate, labelProducer);
    
      while (i < thumbs.length && documentDate.equals(thumbs[i].getDate())) {
        thumbGroup.addThumb(thumbs[i]);
        i++;
      }
      resultGroups.add(thumbGroup);
    }
    return resultGroups;
  }
}
