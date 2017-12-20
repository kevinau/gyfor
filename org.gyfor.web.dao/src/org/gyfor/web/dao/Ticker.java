package org.gyfor.web.dao;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSocketChannel;


//@Context("/ws/entity")
//@Resource(path = "/static", location = "static")
//@Component(service = HttpHandler.class)
public class Ticker extends WebSocketProtocolHandshakeHandler {

  private final String context;
  
  private PartyWebSocketConnectionCallback callback;


  public Ticker() {
    super(new PartyWebSocketConnectionCallback());
    
    Context contextAnn = this.getClass().getAnnotation(Context.class);
    context = contextAnn.value();
  }

  
  private static class PartyWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {

    private int i = 0;

    private class SessionData {
      private Class<?> entityClass;
      private Field[] fields;
    }
    
    @Override
    protected Object buildSessionData (String path, Map<String, String> queryMap) throws IllegalArgumentException {
      SessionData sessionData = new SessionData();
      
//      try {
//        // 'path' should be the fully qualified class name of the entity we are editing
//        sessionData.entityClass = Class.forName(path);
//        String fieldsQuery = queryMap.get("fields");
//        if (fieldsQuery == null) {
//          throw new IllegalArgumentException("No 'fields' query paramenter specified");
//        }
//        String[] fieldNames = fieldsQuery.split(" ");
//        sessionData.fields = new Field[fieldNames.length];
//        int i = 0;
//        for (String fieldName : fieldNames) {
//          try {
//            Field field = sessionData.entityClass.getDeclaredField(fieldName);
//            sessionData.fields[i++] = field;
//          } catch (NoSuchFieldException ex) {
//            throw new IllegalArgumentException("Field '" + fieldName + "' not found within class '" + path + "'");
//          }
//        }
//      } catch (ClassNotFoundException ex) {
//        throw new IllegalArgumentException("Class '" + path + "' not found");
//      } catch (SecurityException ex) {
//        throw new RuntimeException(ex);
//      }
      return sessionData;
    }
    
    
    @Override
    protected void doRequest (String command, String[] args, Object sessionData) {
      Class<?> entityClass = ((SessionData)sessionData).entityClass;
      Field[] fields = ((SessionData)sessionData).fields;
      
      switch (command) {
      case "query" :
        break;
      case "add" :
        // The args should be field values, including any auto generated primary id
        if (args.length < fields.length) {
          throw new IllegalArgumentException("Expecting " + fields.length + " but was given only " + args.length);
        }
        try {
          Object entity = entityClass.newInstance();
          for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            // TODO for the moment we only support integer and String field values.  This code should be replaced
            // by proper plan and model code
            if (Integer.class.isAssignableFrom(field.getType())) {
              int intValue = Integer.parseInt(args[i]);
              field.setAccessible(true);
              field.set(entity, intValue);
            } else {
              field.setAccessible(true);
              field.set(entity, args[i]);
            }
          }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
          throw new RuntimeException(ex);
        }
        
        break;
      case "change" :
        break;
      case "remove" :
        break;
      default :
        throw new RuntimeException("Unrecognised command '" + command + "'");
      }
    }
    
    
    private Timer timer;


    public void startTicking() {
      TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
          i++;
          System.out.println("...........Tick " + i);
          doSendAll ("add", p -> "v" + i);
        }

      };
      timer = new Timer();
      timer.schedule(updateTask, 0, // initial delay
          5 * 1000); // subsequent rate
    }


    public void stopTicking() {
      timer.cancel();
    }


    @Override
    protected void doRequest(Request request, Object sessionData, WebSocketChannel channel) {
    }


    @Override
    protected void openResources() {
    }


    @Override
    protected void closeResources() {
    }

  }


  @Activate
  public void activate(ComponentContext componentContext) {
    callback = CallbackAccessor.getCallback(this);
    callback.startTicking();
  }


  @Deactivate
  public void deactivate() {
    callback.stopTicking();
  }

}
