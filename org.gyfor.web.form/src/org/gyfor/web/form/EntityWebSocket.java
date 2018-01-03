package org.gyfor.web.form;

import java.lang.reflect.Field;

import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


//@Context("/eex")
//@Resource(path="/resources", location="resources")
//@Component(service=HttpHandler.class)
public class EntityWebSocket extends WebSocketProtocolHandshakeHandler {

  private Logger logger = LoggerFactory.getLogger(EntityWebSocket.class);
  
  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  @Reference
  private IPlanFactory planFactory;
  
  @Reference
  private IModelFactory modelFactory;
  
  
  private ITemplateEngine templateEngine;
  
  
  public EntityWebSocket() {
    super (new ProxyWebSocketConnectionCallback());
  }


  @Activate
  public void activate (BundleContext bundleContext) {
    logger.info("Activating");
    templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    
    TokenParser entityTokenParser = new EntityTokenParser();
    templateEngine.addTokenParser(entityTokenParser);
    
    TokenParser fieldTokenParser = new FieldTokenParser();
    templateEngine.addTokenParser(fieldTokenParser);
    
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
      callback.setup(templateEngine, planFactory, modelFactory);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Deactivate
  public void deactivate () {
  }
  
}
