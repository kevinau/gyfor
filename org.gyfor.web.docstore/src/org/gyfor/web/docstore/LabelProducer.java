package org.gyfor.web.docstore;

import java.time.LocalDate;

public interface LabelProducer {

  public String getLabel (int level, LocalDate date);

  public String getLabel (LocalDate date);
  
}
