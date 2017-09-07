package org.gyfor.web.dao;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class Control {

  private final String name;
  private final WebSocketChannel channel;
  
  private ControlMode mode;
  
  
  public Control (String name, WebSocketChannel channel) {
    this.name = name;
    this.channel = channel;
  }
  
  
  public void setMode (ControlMode mode) {
    if (this.mode != mode) {
      this.mode = mode;      
      Response response = new Response("setButton", name, mode.isNA(), mode.isEnabled());
      WebSockets.sendText(response.toString(), channel, null);
    }
  }
  
}
