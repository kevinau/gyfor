package org.gyfor.websocket.dao;


public enum ControlMode {

  ENABLED,
  DISABLED,
  NA;
  
  public boolean isNA() {
    return this == ControlMode.NA;
  }
  
  public boolean isEnabled () {
    return this == ControlMode.ENABLED;
  }
  
}
