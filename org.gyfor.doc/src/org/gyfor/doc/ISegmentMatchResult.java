package org.gyfor.doc;

public interface ISegmentMatchResult {

  public int start();
  
  public int end();
  
  public SegmentType type();
  
  public Object value();
  
}
