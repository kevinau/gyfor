package org.plcore.http.impl;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;


//@Context("/ws")
//@Resource(path="/static", base="static")
//@Component(service=HttpHandler.class)
public class WebSocketExample extends WebSocketProtocolHandshakeHandler {

  public WebSocketExample() {
    super(new WebSocketConnectionCallback() {

      @Override
      public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        channel.getReceiveSetter().set(new AbstractReceiveListener() {

          @Override
          protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
            WebSockets.sendText(message.getData(), channel, null);
          }
        });
        channel.resumeReceives();
      }
    });
  }

}
