package org.gyfor.docstore;


public enum DividendType {

  INTERIM("Interim"),
  FINAL("Final"),
  SPECIAL("Special");
  
  private final String label;
  
  DividendType (String label) {
    this.label = label;
  }
  
  
  public String getLabel () {
    return label;
  }
  
}
