package org.plcore.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.ChannelListener;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;


public abstract class AbstractWebSocketConnectionCallback<S extends ISessionData> implements WebSocketConnectionCallback {
  
  private final Logger logger = LoggerFactory.getLogger(AbstractWebSocketConnectionCallback.class);
  
  private final Map<WebSocketChannel, S> channels = new HashMap<>();

  private String context;

  public void setContext(String context) {
    this.context = context;
  }

  
  @Override
  public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
    String uri = exchange.getRequestURI();
    logger.info("Websocket connect: {}, {}", channel.getSourceAddress(), uri);

    String requestPath = null;
    if (context != null) {
      if (!uri.startsWith(context)) {
        throw new RuntimeException("uri '" + uri + "' should start with " + context);
      }
      requestPath = uri.substring(context.length());
      int n = requestPath.indexOf('?');
      if (n >= 0) {
        requestPath = requestPath.substring(0, n);
      }
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
    S sessionData = buildSessionData(requestPath, queryMap, channel);
    
    synchronized (channels) {
      if (channels.isEmpty()) {
        openResources();
      }
      channels.put(channel, sessionData);
      channel.getCloseSetter().set(new ChannelListener<Channel>() {

        @Override
        public void handleEvent(Channel channel) {
          logger.info("Websocket channel closed: {}", channel);
          synchronized (channels) {
            channels.remove(channel);
            if (channels.isEmpty()) {
              closeResources();
            }
          }
        }
      });
      
    }
    
    channel.getReceiveSetter().set(new AbstractReceiveListener() {

      @Override
      protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
        synchronized (channels) {
          String msg = message.getData();
          logger.info("Websocket receive msg: {}, {}", channel.getSourceAddress(), msg);
          if (msg.equals("close")) {
            try {
              logger.info("Websocket channel close request {}", channel.getSourceAddress());
              channel.close();
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          } else {
            ISessionData sessionData = channels.get(channel);
            WebSocketSession wss = new WebSocketSession(channel);
            
            // Split message into command and args
            String command;
            String[] args;
            int n = msg.indexOf(DELIMITER);
            if (n == -1) {
              command = msg;
              args = new String[0];
            } else {
              command = msg.substring(0, n);
              // Count the number of arguments (the number of delimiters + 1)
              int argn = 1;
              int n1 = msg.indexOf(DELIMITER, n + 1);
              while (n1 != -1) {
                argn++;
                n1 = msg.indexOf(DELIMITER, n1 + 1);
              }
              // Get the arguments
              args = new String[argn];
              int i = 0;
              int n0 = n + 1;
              n1 = msg.indexOf(DELIMITER, n0);
              while (n1 != -1) {
                args[i++] = msg.substring(n0, n1);
                n0 = n1 + 1;
                n1 = msg.indexOf(DELIMITER, n0);
              }
              args[i] = msg.substring(n0); 
            }
            doRequest(command, args, sessionData, wss);
          }
        }
      }
    });

    channel.resumeReceives();
  }
  
  
  protected abstract S buildSessionData (String path, Map<String, String> queryMap, WebSocketChannel channel);

  private static final char DELIMITER = '\t';

  protected String getMessageCommand(String msg) {
    int n = msg.indexOf(DELIMITER);
    if (n == -1) {
      return msg;
    } else {
      return msg.substring(0, n);
    }
  }
  
  
  protected String[] getMessageArgs(String msg) {
    int n = msg.indexOf(DELIMITER);
    if (n == -1) {
      return new String[0];
    } else {
      String[] args;
      
      // Count the number of arguments (the number of delimiters + 1)
      int argn = 1;
      int n1 = msg.indexOf(DELIMITER, n + 1);
      while (n1 != -1) {
        argn++;
        n1 = msg.indexOf(DELIMITER, n1 + 1);
      }
      // Get the arguments
      args = new String[argn];
      int i = 0;
      int n0 = n + 1;
      n1 = msg.indexOf(DELIMITER, n0);
      while (n1 != -1) {
        args[i++] = msg.substring(n0, n1);
        n0 = n1 + 1;
        n1 = msg.indexOf(DELIMITER, n0);
      }
      args[i] = msg.substring(n0); 
      return args;
    }
  }
  
  
  protected abstract void doRequest (String command, String[] args, ISessionData sessionData, WebSocketSession wss);
  
  
  public void closeAllSessions() {
    synchronized (channels) {
      for (WebSocketChannel channel : channels.keySet()) {
        try {
          channel.close();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
      channels.clear();
    }
  }
  
  
  protected void forAllSessions (BiConsumer<WebSocketSession, S> consumer) {
    synchronized (channels) {
      for (Map.Entry<WebSocketChannel, S> entry : channels.entrySet()) {
        WebSocketSession session = new WebSocketSession(entry.getKey());
        consumer.accept(session, entry.getValue());
      }
    }
  }

  
  protected abstract void openResources ();
  
  
  protected abstract void closeResources ();

}
