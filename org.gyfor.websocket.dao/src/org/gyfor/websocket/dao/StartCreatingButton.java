package org.gyfor.websocket.dao;

import io.undertow.websockets.core.WebSocketChannel;

public class StartCreatingButton implements EntityEditStateChangeListener {

  private final Control startButton;
  
  StartCreatingButton (WebSocketChannel channel) {
    startButton = new Control("button-start-creating", channel);
  }
  
  
  @Override
  public void stateChanged(EntityEditState state) {
    switch (state) {
    case CHANGING :
    case CREATING :
    case REMOVING :
    case RETIRING :
    case UNRETIRING :
      startButton.setMode(ControlMode.DISABLED);
      break;
    case CLEAR :
    case EXISTING :
    case EXISTING_RETIRED :
      startButton.setMode(ControlMode.ENABLED);
      break;
    }
  }
  
  
}
