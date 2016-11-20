package org.gyfor.web.form;

public enum Action {

  REMOVE_BY_ID ("R"),
  REMOVE_BY_INDEX ("r"),
  ADD ("A");
  
  private final String code;
  
  private Action(String code) {
    this.code = code;
  }
  
  public String toCode() {
    return code;
  }
  
}
