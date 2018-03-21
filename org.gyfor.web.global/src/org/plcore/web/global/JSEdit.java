package org.plcore.web.global;


public enum JSEdit {

  ADD_CHILD("addChild"),
  ADD_CHILDREN("addChildren"),
  REPLACE_NODE("replaceNode"),
  REPLACE_CHILDREN("replaceChildren"),
  DELETE_CHILDREN("deleteChildren"),
  DELETE_NODES("deleteNodes");
  
  private final String command;
  
  private JSEdit(String command) {
    this.command = command;
  }

  public String command() {
    return command;
  }
  
}
