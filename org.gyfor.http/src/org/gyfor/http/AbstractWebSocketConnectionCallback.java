package org.gyfor.http;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.xnio.ChannelListener;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public abstract class AbstractWebSocketConnectionCallback implements WebSocketConnectionCallback {
  private final List<WebSocketChannel> channels = new ArrayList<WebSocketChannel>();
  

  protected abstract void handleTextMessage(WebSocketSession session, String command, String data);
  
  @Override
  public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
    synchronized (channels) {
      channels.add(channel);
      channel.getCloseSetter().set(new ChannelListener<Channel>() {

        @Override
        public void handleEvent(Channel channel) {
          synchronized (channels) {
            System.out.println("Closing channel because of a close event");
            channels.remove(channel);
          }
        }
      });
      
    }
    
    channel.getReceiveSetter().set(new AbstractReceiveListener() {

      @Override
      protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
        String data = message.getData();
        int n = data.indexOf('|');
        String command;
        if (n == -1) {
          command = data;
        } else {
          command = data.substring(0,  n);
          data = data.substring(n + 1);
        }
        switch (command) {
        case "close" :
          synchronized (channels) {
            channels.remove(channel);
            try {
              channel.close();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          }
          break;
        default :
          WebSocketSession session = new WebSocketSession(channel);
          handleTextMessage(session, command, data);
          break;
        }
        ////WebSockets.sendText(message.getData(), channel, null);
      }
    });

    channel.resumeReceives();
  }
  
  
  public void closeAllSessions() {
    synchronized (channels) {
      for (WebSocketChannel channel : channels) { 
        try {
          channel.close();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }
  
  
  protected void forAllSessions (Consumer<WebSocketSession> consumer) {
    synchronized (channels) {
      for (WebSocketChannel channel : channels) {
        WebSocketSession session = new WebSocketSession(channel);
        consumer.accept(session);
      }
    }
  }
  
}
