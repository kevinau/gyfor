package org.plcore.http;

import java.io.IOException;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;


public class WebSocketSession {

  private final WebSocketChannel channel;
  
  WebSocketSession (WebSocketChannel channel) {
    this.channel = channel;
  }
  
  
  public void send (String text) {
    WebSockets.sendText(text, channel, null);
  }
  
  
  public void send (String command, Object... args) {
    send(channel, command, args);
  }
  
  
  public static void send (WebSocketChannel channel, String command, Object... args) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(command);
    for (Object arg : args) {
      buffer.append('\t');
      if (arg != null) {
        buffer.append(arg.toString());
      }
    }
    System.out.println("WebSocketSession: " + buffer.toString());
    WebSockets.sendText(buffer.toString(), channel, null);    
  }
  
  
  public void close () {
    try {
      channel.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
