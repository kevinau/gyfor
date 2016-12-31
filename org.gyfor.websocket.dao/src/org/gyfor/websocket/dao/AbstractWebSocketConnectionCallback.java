package org.gyfor.websocket.dao;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.ChannelListener;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;


public abstract class AbstractWebSocketConnectionCallback implements WebSocketConnectionCallback {

  private final Logger logger = LoggerFactory.getLogger(PartyEdit.class);
  
  private final Map<WebSocketChannel, Object> sessions = new HashMap<>();

  private String context;
  
  public void setContext(String context) {
    this.context = context;
  }

  
  @Override
  public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
    String uri = exchange.getRequestURI();
    if (!uri.startsWith(context + "/")) {
      throw new RuntimeException("uri '" + uri + "' should start with " + context + "/");
    }
    logger.info("Websocket connect: {}, {}", channel.getSourceAddress(), uri);
    
    String requestPath = uri.substring(context.length() + 1);
    int n = requestPath.indexOf('?');
    if (n >= 0) {
      requestPath = requestPath.substring(0, n);
    }
    
    Map<String, String> queryMap = new LinkedHashMap<>();
    String queryString = exchange.getQueryString();
    if (queryString.length() > 0) {
      String[] segments = queryString.split("&");
      for (String segment : segments) {
        String[] parts = segment.split("=");
        if (parts.length == 1) {
          queryMap.put(parts[0], "");
        } else {
          String value;
          try {
            value = URLDecoder.decode(parts[1], "UTF-8");
          } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
          }
          queryMap.put(parts[0], value);
        }
      }
    }
    Object sessionData = buildSessionData(requestPath, queryMap);
    
    synchronized (sessions) {
      sessions.put(channel, sessionData);
      channel.getCloseSetter().set(new ChannelListener<Channel>() {
        @Override
        public void handleEvent(Channel channel) {
          logger.info("Websocket channel closed: {}", channel);
          synchronized (sessions) {
            sessions.remove(channel);
          }
        }
      });
    }
    
    channel.getReceiveSetter().set(new AbstractReceiveListener() {
      @Override
      protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
        synchronized (sessions) {
          String data = message.getData();
          logger.info("Websocket receive msg: {}, {}", channel.getSourceAddress(), data);
          if (data.equals("close")) {
            sessions.remove(channel);
            try {
              logger.info("Websocket channel close request {}", channel.getSourceAddress());
              channel.close();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          } else {
            int n = data.indexOf('|');
            String command;
            String[] args;
            if (n == -1) {
              command = data;
              args = new String[0];
            } else {
              command = data.substring(0, n);
              args = data.substring(n + 1).split("\\|");
            }
            Object sessionData = sessions.get(channel);
            doRequest(command, args, sessionData);
          }
          //// WebSockets.sendText(message.getData(), channel, null);
        }
      }
    });

    channel.resumeReceives();
  }
  
  
  protected abstract Object buildSessionData (String path, Map<String, String> queryMap);
  
  
  protected abstract void doRequest (String command, String[] args, Object sessionData);
  
  
  protected void doSendAll (String command, Function<Object, String> stringBuilder) {
    synchronized (sessions) {
      for (WebSocketChannel session : sessions.keySet()) {
        Object sessionData = sessions.get(session);
        String msg = command + '|' + stringBuilder.apply(sessionData);
        logger.info("Websocket send message {}, {}", session.getSourceAddress(), msg);
        WebSockets.sendText(msg, session, null);
      }
    }

  }
}
