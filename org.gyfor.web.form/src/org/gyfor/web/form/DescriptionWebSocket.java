package org.gyfor.web.form;


import java.util.Map;

import org.gyfor.value.EntityLife;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.plcore.dao.IDataAccessObject;
import org.plcore.http.AbstractWebSocketConnectionCallback;
import org.plcore.http.CallbackAccessor;
import org.plcore.http.Context;
import org.plcore.http.ISessionData;
import org.plcore.http.WebSocketSession;
import org.plcore.template.ITemplateEngineFactory;
import org.plcore.userio.desc.DescriptionFactory;
import org.plcore.userio.model.IModelFactory;
import org.osgi.service.event.EventConstants;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSocketChannel;


@Context("/edesc")
@Component(service = {HttpHandler.class, EventHandler.class},
           property = EventConstants.EVENT_TOPIC + "=" + IDataAccessObject.EVENT_BASE + "*")
public class DescriptionWebSocket extends WebSocketProtocolHandshakeHandler implements EventHandler {

  @Reference
  private IModelFactory modelFactory;

  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  
  private DescriptionWebSocketConnectionCallback callback;
  
  
  public DescriptionWebSocket() {
    super (new DescriptionWebSocketConnectionCallback());
  }
  
  
  private static class DescriptionWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback<DescriptionSessionData> {
    
    @Override
    protected DescriptionSessionData buildSessionData(String path, Map<String, String> queryMap, WebSocketChannel channel) {
      System.out.println("Description: build session data");
      
      if (path == null || path.length() == 0) {
        throw new IllegalArgumentException("No class name specified");
      }
      // Assuming the path starts with a slash (/)
      path = path.substring(1);
      return new DescriptionSessionData(path);
    }


    @Override
    protected void doRequest(String command, String[] args, ISessionData sessionData, WebSocketSession wss) {
      System.out.println("Description: doRequest " + command);
      switch (command) {
      case "hello" :
        sessionData.startSession();
        break;
      default :
        throw new RuntimeException("Unknown command: '" + command + "'");
      }
    }


    @Override
    protected void openResources() {
    }


    @Override
    protected void closeResources() {
    }
    
    
    private void handleEvent(Event event) {
      String topic = event.getTopic();
      System.out.println("description: handleEvent " + topic);
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
      Object value = event.getProperty("object");
      String entityClassName = value.getClass().getCanonicalName();
      forAllSessions((wss, sessionData) -> {
        if (sessionData.getEntityClassName().equals(entityClassName)) {
          int id = (Integer)event.getProperty("id");
          if (topic.endsWith("/REMOVED")) {
            wss.send("setDescription", id);
          } else {
            String text = DescriptionFactory.getDescription(value);
            EntityLife entityLife = (EntityLife)event.getProperty("entityLife");
            wss.send("setDescription", id, text, entityLife);
          }
        }
      });
    }
  }
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    callback = CallbackAccessor.getCallback(this);
  }
  
  
  @Deactivate 
  public void deactivate () {
    callback.closeAllSessions();
  }


  @Override
  public void handleEvent(Event event) {
    callback.handleEvent(event);
  }

}
