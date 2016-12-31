package org.gyfor.http;

import java.lang.reflect.Field;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;

public class CallbackAccessor {
  
  @SuppressWarnings("unchecked")
  public static <X extends WebSocketConnectionCallback> X getCallback (WebSocketProtocolHandshakeHandler component) {  
    // The following nastiness is required because the WebSocketConnectionCallback 
    // can only be initialized in the constructor, but we need to provide configuration
    // at the time this component is activated.
    try {
      Class<?> entityWebsocketClass = component.getClass();
      Class<?> handshakeHandlerClass = entityWebsocketClass.getSuperclass();
      Field callbackField = handshakeHandlerClass.getDeclaredField("callback");
      callbackField.setAccessible(true);
      WebSocketConnectionCallback callback = (WebSocketConnectionCallback)callbackField.get(component);
      return (X)callback;
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
