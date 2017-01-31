package org.gyfor.websocket.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gyfor.berkeleydb.DataEnvironment;
import org.gyfor.berkeleydb.DataTable;
import org.gyfor.berkeleydb.ObjectDatabaseEntry;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Party;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

/**
 * A web socket that provides a web pages with all the information it needs for an entity 
 * edit form.  Specifically, it provides:
 * <p>
 * <dl><dt>list</dt><dd>A list of all entities.  It is assumed this list is not huge.  This list is used
 * to select an entity.  For each entity, the list provides:
 *   <ul><li>A label.  This will be a concatenation of the entity 'description' fields</li>
 *   <li>A value.  This will be the entity 'id' field</li>
 *   </ul>
 * </dd>
 * <dt>query &lt;search&gt;</dt><dd>Return a description of the entity, given a search value.  This is used
 * to verify foreign key fields and display an associated description.</dd>
 * <dt>fetch &lt;id&gt;</dt><dd>Return all the fields of a single entity, identified by 'id'.</dd>
 * <dt>update &lt;value...&gt;</dt><dd>Update a single entity, using the values provided.  The first
 * value should be the entity 'id'.  If the entity does not exist, add it.  If the entity does exist, update it.</dd>
 * <dt>remove &lt;id&gt;</dt><dd>Remove the single entity identified by 'id'.</dd>
 * </dl>
 * If the requested action cannot be completed, an error response is generated.
 * <p>
 * In addition, if an entity is updated or removed, a notification is sent so the edit form can update 
 * its list of all entities.
 * <p>
 * All commands and responses are tab delimited fields, with the first field the command or response name.
 */
//@Context("/ws/entity")
//@Resource(path = "/static", location = "static")
//@Component(service = HttpHandler.class)
public class DataAccessObjectWebSocket2 extends WebSocketProtocolHandshakeHandler {

  private final EntityWebSocketConnectionCallback callback;
  private final String context;
  
  private IPlanContext planContext;
  private DataEnvironment dataEnvironment;
  private ITemplateEngineFactory templateEngineFactory;
  private Class<?> entityClass = Party.class;
  
  private DataTable entityTable;
  

  public DataAccessObjectWebSocket2() {
    super(new EntityWebSocketConnectionCallback());
    
    callback = CallbackAccessor.getCallback(this);

    Context contextAnn = this.getClass().getAnnotation(Context.class);
    context = contextAnn.value();
  }

  
  @Reference
  public void setPlanContext (IPlanContext planContext) {
    this.planContext = planContext;
  }
  
  
  public void unsetPlanContext (IPlanContext planContext) {
    this.planContext = null;
  }
  
  
  @Reference
  public void setDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = dataEnvironment;
  }
  
  
  public void unsetDataEnvironment (DataEnvironment dataEnvironment) {
    this.dataEnvironment = null;
  }
  
  
  @Reference
  public void setTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = templateEngineFactory;
  }
  
  
  public void unsetTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = null;
  }
  
  
  @Activate
  public void activate(ComponentContext componentContext) {
    callback.setContext(context);
    
    callback.setPlanEnvironment(planContext);
    callback.setDataEnvironment(dataEnvironment);
    callback.setEntityClass(entityClass);
    
    ITemplateEngine templateEngine = templateEngineFactory.buildTemplateEngine(componentContext);
    callback.setTemplateEngine(templateEngine);
  }


  @Deactivate
  public void deactivate() {
    entityTable.close();
  }

  
  protected static class EntityWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {

    private final Logger logger = LoggerFactory.getLogger(DataAccessObjectWebSocket2.class);
    
    private IPlanContext planContext;
    private DataEnvironment dataEnvironment;
    private ITemplateEngine templateEngine;
    
    private IEntityPlan<?> entityPlan;
    private DataTable entityTable;
    private Class<?> entityClass;
     
    
    private void setPlanEnvironment (IPlanContext planContext) {
      this.planContext = planContext;
    }
    
    
    private void setTemplateEngine (ITemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
    }
    
    
    private void setDataEnvironment (DataEnvironment dataEnvironment) {
      this.dataEnvironment = dataEnvironment;
    }
    
    
    private void setEntityClass (Class<?> entityClass) {
      this.entityClass = entityClass;
    }
    
    
    @Override
    protected Object buildSessionData (String path, Map<String, String> queryMap) throws IllegalArgumentException {
      return null;
    }
    
    
    @Override
    protected void openResources () {
      logger.info("Opening resources for {}", entityClass);
      
      entityPlan = planContext.getEntityPlan(entityClass);
      entityTable = dataEnvironment.openTable(entityPlan, false); 
    }

 
    @Override
    protected void doRequest (Request request, Object sessionData, WebSocketChannel channel) {
      logger.info("Performing request {}", request.getName());
      
      switch (request.getName()) {
      case "describeAll" :
        doDescribeAllRequest(channel);
        break;
      case "query" :
        break;
      case "fetch" :
        break;
      case "update" :
//        // The args should be field values, including any auto generated primary id
//        if (args.length < fields.length) {
//          throw new IllegalArgumentException("Expecting " + fields.length + " but was given only " + args.length);
//        }
//        try {
//          Object entity = entityClass.newInstance();
//          for (int i = 0; i < fields.length; i++) {
//            Field field = fields[i];
//            // TODO for the moment we only support integer and String field values.  This code should be replaced
//            // by proper plan and model code
//            if (Integer.TYPE.isAssignableFrom(field.getType())) {
//              int intValue = Integer.parseInt(args[i]);
//              field.setAccessible(true);
//              field.set(entity, intValue);
//            } else {
//              field.setAccessible(true);
//              field.set(entity, args[i]);
//            }
//          }
//          PrimaryIndex<Integer, Object> index = dataStore.getPrimaryIndex(Integer.class, entityClass);
//          index.put(entity);
//          
//          // Test the entity add
//          EntityCursor<Object> indexCursor = index.entities();
//          try {
//            for (Object doc : indexCursor) {
//              System.out.println("=============== " + doc);
//            }
//          } finally {
//            indexCursor.close();
//          } 
//
//        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
//          throw new RuntimeException(ex);
//        }
        break;
      case "remove" :
        break;
      default :
        throw new RuntimeException("Unrecognised command '" + request.getName() + "'");
      }
    }
  
  
    @Override
    protected void closeResources () {
      logger.info("Closing resources");
      
      entityTable.close();
      entityTable = null;
      entityPlan = null;
    }
    
    
    public static class EntityDescription implements Comparable<EntityDescription> {
      private final String description;
      private final int id;
      
      private EntityDescription (String description, int id) {
        this.description = description;
        this.id = id;
      }

      @Override
      public int compareTo(EntityDescription other) {
        return description.compareTo(other.description);
      }

      @Override
      public String toString() {
        return "EntityDescription [" + description + ", " + id + "]";
      }
      
      public String getDescription () {
        return description;
      }
      
      public int getId () {
        return id;
      }
    }

    
    private void doDescribeAllRequest (WebSocketChannel channel) {
      List<EntityDescription> descriptions = new ArrayList<>();
      try (Cursor cursor = entityTable.openCursor()) {
        ObjectDatabaseEntry data = new ObjectDatabaseEntry(entityPlan);
      
        OperationStatus status = cursor.getFirst(null, data, LockMode.DEFAULT);
        while (status == OperationStatus.SUCCESS) {
          Object value = data.getValue();
          EntityDescription ed = new EntityDescription(entityPlan.getDescription(value), entityPlan.getId(value));
          descriptions.add(ed);
          status = cursor.getNext(null, data, LockMode.DEFAULT);
        }
      }
      Collections.sort(descriptions);
      
      System.out.println("Descriptions:");
      for (EntityDescription ed : descriptions) {
        System.out.println(">>>>>>>>>>>>>>>>>>>> " + ed);
      }
      System.out.println();
      
      ITemplate template = templateEngine.getTemplate("describeList");
      template.putContext("descriptions", descriptions);
      String response = template.evaluate();
      System.out.println(">>>>>>>" + response + "<<<<<<<");
      WebSockets.sendText(response, channel, null);
    }
  }  
  
}
