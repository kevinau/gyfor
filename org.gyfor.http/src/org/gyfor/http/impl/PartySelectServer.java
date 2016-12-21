package org.gyfor.http.impl;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.osgi.service.component.annotations.Component;
import org.xnio.ChannelListener;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;


@Context("/partySelect")
@Resource(path = "/static", location = "static")
@Component(service = HttpHandler.class)
public class PartySelectServer extends WebSocketProtocolHandshakeHandler {

  private static final List<WebSocketChannel> sessions = new ArrayList<WebSocketChannel>();
  
  private static int i = 0;

  public PartySelectServer() {
    super(new WebSocketConnectionCallback() {

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
          Timer timer = new Timer();
          timer.schedule(updateTask,
                         0,          //initial delay
                         5 * 1000);  //subsequent rate
        }
////        channel.getReceiveSetter().set(new AbstractReceiveListener() {
////
////          @Override
////          protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
////            WebSockets.sendText(message.getData(), channel, null);
////          }
////        });
//        channel.resumeReceives();
      }
    });
  }  
  
}
