package org.gyfor.http.impl;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gyfor.http.CallbackAccessor;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.xnio.ChannelListener;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;


//@Context("/partySelect")
//@Resource(path = "/static", location = "static")
//@Component(service = HttpHandler.class)
public class PartySelectSocket extends WebSocketProtocolHandshakeHandler {

  private PartyWebSocketConnectionCallback callback;
  
  
  public PartySelectSocket() {
    super (new PartyWebSocketConnectionCallback());
  }
  
  
  private static class PartyWebSocketConnectionCallback implements WebSocketConnectionCallback {
    private final List<WebSocketChannel> sessions = new ArrayList<WebSocketChannel>();
    
    private int i = 0;

    
    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
      synchronized (sessions) {
        sessions.add(channel);
        channel.getCloseSetter().set(new ChannelListener<Channel>() {

          @Override
          public void handleEvent(Channel channel) {
            synchronized (sessions) {
              sessions.remove(channel);
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
            synchronized (sessions) {
              sessions.remove(channel);
              try {
                channel.close();
              } catch (IOException ex) {
                throw new RuntimeException(ex);
              }
            }
            break;
          }
          ////WebSockets.sendText(message.getData(), channel, null);
        }
      });
  
      channel.resumeReceives();
    }
    
    private Timer timer;
    
    
    public void startTicking () {
      TimerTask updateTask = new TimerTask () {

        @Override
        public void run() {
          synchronized (sessions) {
            i++;
            System.out.println("........ ticking " + i);
            for (WebSocketChannel session : sessions) {
              System.out.println(".......... sending to " + session);
              WebSockets.sendText("add|v" + i + "|Label " + i, session, null);
            }
          }
        }
        
      };
      timer = new Timer();
      timer.schedule(updateTask,
                     0,          //initial delay
                     5 * 1000);  //subsequent rate
    }
    
    
    public void stopTicking () {
      timer.cancel();
    }
  
  }
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    callback = CallbackAccessor.getCallback(this);
    callback.startTicking();
  }
  
  
  @Deactivate 
  public void deactivate () {
    callback.stopTicking();
  }
  
}
