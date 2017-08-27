package org.gyfor.http;

import java.io.IOException;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class WebSocketSession {

  private final WebSocketChannel channel;
  
  WebSocketSession (WebSocketChannel channel) {
    this.channel = channel;
  }
  
  
  public void sendText (String text) {
    WebSockets.sendText(text, channel, null);
  }
  
  
  public void sendText (String command, String... args) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(command);
    for (String arg : args) {
      buffer.append('|');
      buffer.append(arg);
    }
    sendText(buffer.toString());
  }
  
  
  public void close () {
    try {
      channel.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
