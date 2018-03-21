package org.gyfor.web.docstore;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.plcore.http.AbstractWebSocketConnectionCallback;
import org.plcore.http.CallbackAccessor;
import org.plcore.http.Context;
import org.plcore.http.Resource;
import org.plcore.http.WebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


////@Context("/d/edit")
////@Resource(path = "/resources", location = "resources")
////@Component(service = HttpHandler.class)
public class DocumentDataEdit extends WebSocketProtocolHandshakeHandler {

  private EditWebSocketConnectionCallback callback;
  
  
  public DocumentDataEdit() {
    super (new EditWebSocketConnectionCallback());
  }
  
  
  private static class EditWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {

    private Logger logger = LoggerFactory.getLogger(DocumentDataEdit.class);
    
    @Override
    protected void handleTextMessage(WebSocketSession session, String command, String data) {
      logger.info("handleTextMessage: {}: {}", command, data);
      switch (command) {
      case "setType" :
        break;
      case "set" :
        break;
      default :
        throw new IllegalArgumentException("Command '" + command + "' not recognised");
      }
    }
  
  }
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    callback = CallbackAccessor.getCallback(this);
    //callback.startTicking();
  }
  
  
  @Deactivate 
  public void deactivate () {
    //callback.stopTicking();
  }
  
}
