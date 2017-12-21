package org.gyfor.http.impl;

import java.time.LocalTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.gyfor.http.AbstractWebSocketConnectionCallback;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.WebSocketSession;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


//@Context("/ticktoc")
//@Resource(path = "/resources", location = "static")
//@Component(service = HttpHandler.class)
public class TicktocWebSocket extends WebSocketProtocolHandshakeHandler {

  private TicktocWebSocketConnectionCallback callback;
  
  
  public TicktocWebSocket() {
    super (new TicktocWebSocketConnectionCallback());
  }
  
  
  private static class TicktocWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {
    private int i = 0;

    
    
    private Timer timer;
    
    
    public void startTicking () {
      TimerTask updateTask = new TimerTask () {

        @Override
        public void run() {
          i++;
          System.out.println("........ ticking " + i);
          TicktocWebSocketConnectionCallback.this.forAllSessions(session -> {
            System.out.println(".......... sending to " + session + "   " + LocalTime.now());
            if (i % 10 == 0) {
              session.sendText("replaceChildren|#history|<div>Tick " + i + "</div>");
            } else {
              session.sendText("addChild|#history|<div>Tick " + i + "</div>");
            }
            session.sendText("replaceNode|#latest|<div id='latest'>Toc " + i + "</div>");
          });
        }
        
      };
      System.out.println("..... start ticking");
      i = 0;
      timer = new Timer();
      timer.schedule(updateTask,
                     0,          //initial delay
                     5 * 1000);  //subsequent rate
    }
    
    
    public void stopTicking () {
      timer.cancel();
    }


    protected void handleTextMessage(String command, String data) {
      switch (command) {
      case "reset" :
        stopTicking();
        startTicking();
        break;
      default :
        throw new RuntimeException("Unknown command: '" + command + "'");
      }
    }


    @Override
    protected Object buildSessionData(String path, Map<String, String> queryMap) {
      // TODO Auto-generated method stub
      return null;
    }


    @Override
    protected void doRequest(String command, String[] args, Object sessionData, WebSocketSession wss) {
      // TODO Auto-generated method stub
      
    }


    @Override
    protected void openResources() {
      // TODO Auto-generated method stub
      
    }


    @Override
    protected void closeResources() {
      // TODO Auto-generated method stub
      
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
    callback.closeAllSessions();
  }
  
}
