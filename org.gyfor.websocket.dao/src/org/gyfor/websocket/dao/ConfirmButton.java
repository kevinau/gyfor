package org.gyfor.websocket.dao;

import io.undertow.websockets.core.WebSocketChannel;

public class ConfirmButton implements EntityEditStateChangeListener {

  private final Control button;
  
  private EntityEditState state;
  private boolean isIncomplete;
  private boolean isError;
  
  
  ConfirmButton (WebSocketChannel channel) {
    this.button = new Control("button-confirm", channel);
  }
  
  
  @Override
  public void stateChanged(EntityEditState state) {
    this.state = state;
    setButtonMode();
  }
  
  
  private void setButtonMode () {
    switch (state) {
    case CHANGING :
    case CREATING :
    case REMOVING :
    case RETIRING :
    case UNRETIRING :
      if (isIncomplete || isError) {
        button.setMode(ControlMode.DISABLED);
      } else {
        button.setMode(ControlMode.ENABLED);
      }
      break;
    case CLEAR :
    case EXISTING :
    case EXISTING_RETIRED :
      button.setMode(ControlMode.NA);
      break;
    }
  }

}
