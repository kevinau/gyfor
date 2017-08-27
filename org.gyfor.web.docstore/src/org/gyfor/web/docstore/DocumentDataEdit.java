package org.gyfor.web.docstore;

import java.util.Timer;
import java.util.TimerTask;

import org.gyfor.http.AbstractWebSocketConnectionCallback;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


//@Context("/d/edit")
//@Resource(path = "/static", location = "static")
//@Component(service = HttpHandler.class)
public class DocumentDataEdit extends WebSocketProtocolHandshakeHandler {

  private EditWebSocketConnectionCallback callback;
  
  
  public DocumentDataEdit() {
    super (new EditWebSocketConnectionCallback());
  }
  
  
  private static class EditWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {

    private Logger logger = LoggerFactory.getLogger(DocumentDataEdit.class);
    
    private int i = 0;

    
    private Timer timer;
    
    
    public void startTicking () {
      TimerTask updateTask = new TimerTask () {

        @Override
        public void run() {
          i++;
          System.out.println("........ ticking " + i);
          forAllSessions (session -> {
            System.out.println(".......... sending to " + session);
            session.sendText("add", "v" + i, "Label " + i);
          });
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


    @Override
    protected void handleTextMessage(String command, String data) {
      logger.info("handleTextMessage: {}: {}", command, data);
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
