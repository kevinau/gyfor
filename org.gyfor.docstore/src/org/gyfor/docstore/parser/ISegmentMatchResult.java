package org.gyfor.docstore.parser;

import org.gyfor.docstore.SegmentType;

public interface ISegmentMatchResult {

  public int start();
  
  public int end();
  
  public SegmentType type();
  
  public Object value();
  
}
