package org.gyfor.websocket.dao;

import io.undertow.websockets.core.WebSocketChannel;

public class ResetButton implements EntityEditStateChangeListener {

  private final Control button;
  
  
  ResetButton (WebSocketChannel channel) {
    this.button = new Control("button-confirm", channel);
  }
  
  
  @Override
  public void stateChanged(EntityEditState state) {
    switch (state) {
    case CHANGING :
    case CREATING :
    case REMOVING :
    case RETIRING :
    case UNRETIRING :
      button.setMode(ControlMode.ENABLED, "Cancel");
      break;
    case CLEAR :
      button.setMode(ControlMode.NA);
      break;
    case EXISTING :
    case EXISTING_RETIRED :
      button.setMode(ControlMode.ENABLED, "OK");
      break;
    }
  }

}
