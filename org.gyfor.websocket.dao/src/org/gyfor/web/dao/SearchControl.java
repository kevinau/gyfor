package org.gyfor.web.dao;

import io.undertow.websockets.core.WebSocketChannel;

public class SearchControl implements EntityEditStateChangeListener {

  private Control control;
  
  SearchControl (WebSocketChannel channel) {
    this.control = new Control("control-search", channel);
  }

  @Override
  public void stateChanged(EntityEditState state) {
    switch (state) {
    case CHANGING :
    case REMOVING :
    case RETIRING :
    case UNRETIRING :
      control.setMode(ControlMode.DISABLED);
      break;
    case CLEAR :
    case CREATING :
    case EXISTING :
    case EXISTING_RETIRED :
      control.setMode(ControlMode.ENABLED);
      break;
    }
  }
}
