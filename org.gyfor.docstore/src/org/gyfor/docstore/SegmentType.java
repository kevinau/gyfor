package org.gyfor.docstore;

public enum SegmentType {

  TEXT(true, false),
  DATE(false, false),
  CURRENCY(false, false),
  ABN(false, true),
  PERCENT(false, false),
  RUBBISH(true, false);          // This is only used for analyzing OCR'd data
  
  private final boolean rawText;
  private final boolean singular;
  
  private SegmentType (boolean rawText, boolean singular) {
    this.rawText = rawText;
    this.singular = singular;
  }
  
  
  public boolean isRawText() {
    return rawText;
  }
  
  
  public boolean isSingular() {
    return singular;
  }
  
}
