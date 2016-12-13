package org.gyfor.web.form;

import java.lang.reflect.Field;

import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.object.context.PlanFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


@Context("/ws")
@Resource(path="/resources", location="resources")
@Component(service=HttpHandler.class)
public class EntityWebSocket extends WebSocketProtocolHandshakeHandler {

  private Logger logger = LoggerFactory.getLogger(EntityWebSocket.class);
  
  private BundleContext defaultContext;
  private BundleContext globalContext;
  
  private PlanFactory objectContext;
  
  
  public EntityWebSocket() {
    super (new ProxyWebSocketConnectionCallback());
  }


  @Reference (cardinality = ReferenceCardinality.OPTIONAL)
  public void setGlobalTemplateLocation (IGlobalTemplateLocation globalTemplateLocation) {
    this.globalContext = globalTemplateLocation.getBundleContext();
  }
  
  
  public void unsetGlobalTemplateLocation (IGlobalTemplateLocation globalTemplateLocation) {
    this.globalContext = null;
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    this.defaultContext = bundleContext;
    
    objectContext = new PlanFactory();
    
    // The following nastiness is required because the WebSocketConnectionCallback 
    // can only be initialized in the constructor, but we need to provide configuration
    // at the time this component is activated.
    try {
      Class<?> entityWebsocketClass = this.getClass();
      Class<?> handshakeHandlerClass = entityWebsocketClass.getSuperclass();
      Field callbackField = handshakeHandlerClass.getDeclaredField("callback");
      callbackField.setAccessible(true);
      logger.info("Setting websocket call backto: {}", this);
      ProxyWebSocketConnectionCallback callback = (ProxyWebSocketConnectionCallback)callbackField.get(this);
      callback.setup(defaultContext, globalContext, objectContext);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Deactivate
  public void deactivate () {
  }
  
}
