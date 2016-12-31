package org.gyfor.websocket.dao;

import java.lang.reflect.Field;
import java.util.Map;

import org.gyfor.berkeleydb.DataStore;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


@Context("/ws/entity")
@Resource(path = "/static", location = "static")
@Component(service = HttpHandler.class)
public class EntityEdit extends WebSocketProtocolHandshakeHandler {

  private final String context;
  
  private EntityWebSocketConnectionCallback callback;

  private DataStore dataStore;
  

  public EntityEdit() {
    super(new EntityWebSocketConnectionCallback());
    
    Context contextAnn = this.getClass().getAnnotation(Context.class);
    context = contextAnn.value();
  }

  
  @Reference
  public void setDataStore (DataStore dataStore) {
    this.dataStore = dataStore;
  }
  
  
  public void unsetDataStore (DataStore dataStore) {
    this.dataStore = null;
  }
  
  
  private static class EntityWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {

    private class SessionData {
      private Class<Object> entityClass;
      private Field[] fields;
    }
    

    private DataStore dataStore;
    
    
    private void init (DataStore dataStore) {
      this.dataStore = dataStore;
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    protected Object buildSessionData (String path, Map<String, String> queryMap) throws IllegalArgumentException {
      SessionData sessionData = new SessionData();
      
      try {
        // 'path' should be the fully qualified class name of the entity we are editing
        sessionData.entityClass = (Class<Object>)Class.forName(path);
        String fieldsQuery = queryMap.get("fields");
        if (fieldsQuery == null) {
          throw new IllegalArgumentException("No 'fields' query paramenter specified");
        }
        String[] fieldNames = fieldsQuery.split(" ");
        sessionData.fields = new Field[fieldNames.length];
        int i = 0;
        for (String fieldName : fieldNames) {
          try {
            Field field = sessionData.entityClass.getDeclaredField(fieldName);
            sessionData.fields[i++] = field;
          } catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException("Field '" + fieldName + "' not found within class '" + path + "'");
          }
        }
      } catch (ClassNotFoundException ex) {
        throw new IllegalArgumentException("Class '" + path + "' not found");
      } catch (SecurityException ex) {
        throw new RuntimeException(ex);
      }
      return sessionData;
    }
    
    
    @Override
    protected void doRequest (String command, String[] args, Object sessionData) {
      Class<Object> entityClass = ((SessionData)sessionData).entityClass;
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
            System.out.println("................." + field.getType());
            if (Integer.TYPE.isAssignableFrom(field.getType())) {
              int intValue = Integer.parseInt(args[i]);
              field.setAccessible(true);
              field.set(entity, intValue);
            } else {
              field.setAccessible(true);
              field.set(entity, args[i]);
            }
          }
          PrimaryIndex<Integer, Object> index = dataStore.getPrimaryIndex(Integer.class, entityClass);
          index.put(entity);
          
          // Test the entity add
          EntityCursor<Object> indexCursor = index.entities();
          try {
            for (Object doc : indexCursor) {
              System.out.println("=============== " + doc);
            }
          } finally {
            indexCursor.close();
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
  }
  
    
  @Activate
  public void activate(ComponentContext componentContext) {
    callback = CallbackAccessor.getCallback(this);
    callback.setContext(context);
    callback.init(dataStore);
  }


  @Deactivate
  public void deactivate() {
  }

}
