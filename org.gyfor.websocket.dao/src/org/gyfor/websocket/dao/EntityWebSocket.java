package org.gyfor.websocket.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.gyfor.dao.EntityDescription;
import org.gyfor.dao.IDataAccessObject;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.impl.EntityModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Context("/ws/entity")
@Resource(path = "/static", location = "static")
@Component(service = HttpHandler.class, immediate=true)
public class EntityWebSocket extends WebSocketProtocolHandshakeHandler {

  private final EntityWebSocketConnectionCallback callback;
  private final String context;
  
  private ITemplateEngineFactory templateEngineFactory;
  

  public EntityWebSocket() {
    super(new EntityWebSocketConnectionCallback());
    
    callback = CallbackAccessor.getCallback(this);

    Context contextAnn = this.getClass().getAnnotation(Context.class);
    context = contextAnn.value();
  }

  
  @Reference
  public void setTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = templateEngineFactory;
  }
  
  
  public void unsetTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = null;
  }
  
  
  @Activate
  public void activate(BundleContext bundleContext) {
    callback.setContext(context);
    
    callback.setBundleContext(bundleContext);
    
    ITemplateEngine templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    callback.setTemplateEngine(templateEngine);
    
    
  }


  @Deactivate
  public void deactivate() {
  }

  
  @SuppressWarnings("rawtypes")
  private static class SessionData {
    private final IDataAccessObject<?> dao;
    private final ServiceReference<IDataAccessObject> serviceRef;
    private IEntityModel entityModel;
    
    private SessionData (IDataAccessObject<?> dao, ServiceReference<IDataAccessObject> ref) {
      this.dao = dao;
      this.serviceRef = ref;
    }
  }
  
  
  protected static class EntityWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {

    private final Logger logger = LoggerFactory.getLogger(EntityWebSocket.class);
    
    private BundleContext bundleContext;
    private ITemplateEngine templateEngine;
    
    
    private void setBundleContext (BundleContext bundleContext) {
      this.bundleContext = bundleContext;
    }
    
    
    private void setTemplateEngine (ITemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
    }
    
    
    public static class DataChangeEventHandler implements EventHandler {
      @Override
      public void handleEvent(Event event) {
        int id = (int)event.getProperty("id");
        String description = (String)event.getProperty("description");
        if (description != null) {
          System.err.println("Event handler: " + event.getTopic() + ": " + id + ": " + description);
        }
      }
    }

    
    private static class EntityContainerChangeHandler implements ContainerChangeListener {
      private final WebSocketChannel channel;
      private final ITemplateEngine templateEngine;
      
      private EntityContainerChangeHandler(WebSocketChannel channel, ITemplateEngine templateEngine) {
        this.channel = channel;
        this.templateEngine = templateEngine;
      }
      
      @Override
      public void childAdded(IContainerModel parent, INodeModel node) {
        ITemplate nodeTempl = templateEngine.getTemplate("node");
        nodeTempl.putContext("model", node);
        
        INodePlan plan = node.getPlan();
        nodeTempl.putContext("plan", node.getPlan());
        if (plan instanceof IItemPlan) {
          nodeTempl.putContext("type", ((IItemPlan<?>)plan).getType());
        }
        String html = nodeTempl.evaluate();
        Response response = new Response("update", parent.getNodeId(), node.getNodeId(), html);
        WebSockets.sendText(response.toString(), channel, null);
      }

      @Override
      public void childRemoved(IContainerModel parent, INodeModel node) {
        Response response = new Response("update", "node" + parent.getNodeId(), "node" + node.getNodeId(), "");
        WebSockets.sendText(response.toString(), channel, null);
      }
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    protected Object buildSessionData (String path, Map<String, String> queryMap, WebSocketChannel channel) throws IllegalArgumentException {
      String className = path;
      try {
        Collection<ServiceReference<IDataAccessObject>> refs = bundleContext.getServiceReferences(IDataAccessObject.class, "(name=" + path + ")");
        switch (refs.size()) {
        case 0 :
          throw new IllegalArgumentException("No IDataAccessObject with (class=" + className + ")");
        case 1 :
          ServiceReference<IDataAccessObject> ref = refs.iterator().next();
          IDataAccessObject dao = bundleContext.getService(ref);
          
          // Register this session as an event listener
          String[] topics = new String[] {
              "org/gyfor/data/DataAccessObject/*"
          };
          Dictionary<String, Object> props = new Hashtable<>();
          props.put(EventConstants.EVENT_TOPIC, topics);
          bundleContext.registerService(EventHandler.class.getName(), new DataChangeEventHandler() , props);

          return new SessionData(dao, ref);
        default :
          throw new RuntimeException("Multiple IDataAccessObject's with (class=" + className + ")");
        }
      } catch (InvalidSyntaxException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    
    protected void destroySessionData (Object sessionData) {
      bundleContext.ungetService(((SessionData)sessionData).serviceRef);
    }
    
    
    @Override
    protected void openResources () {
    }

 
    @Override
    protected void doRequest (Request request, Object sessionObj, WebSocketChannel channel) {
      logger.info("Performing request {}", request.getName());
      System.err.println(request.getName());
      System.err.println(request);
      
      switch (request.getName()) {
      case "descriptions" :
        IDataAccessObject<?> dao = ((SessionData)sessionObj).dao;
        doDescriptions (channel, dao);
        break;
      case "init" :
//        SessionData sessionData = (SessionData)sessionObj;
//        IDataAccessObject<?> dao2 = sessionData.dao;
        doInit (channel, (SessionData)sessionObj);
        break;
      default :
        throw new RuntimeException("Unrecognised command '" + request.getName() + "'");
      }
    }
  
  
    @Override
    protected void closeResources () {
      logger.info("Closing resources");
    }
    
    
    private void doDescriptions (WebSocketChannel channel, IDataAccessObject<?> dao) {
      List<EntityDescription> selectList = dao.getDescriptionAll();
      Collections.sort(selectList);

      ITemplate template = templateEngine.getTemplate("descriptionList");
      template.putContext("descriptions", selectList);
      String response = template.evaluate();
      WebSockets.sendText(response, channel, null);
    }

    
    private void doInit (WebSocketChannel channel, SessionData sessionData) {
      IEntityPlan<?> entityPlan = sessionData.dao.getEntityPlan();
      sessionData.entityModel = new EntityModel(entityPlan);
      sessionData.entityModel.addContainerChangeListener(new EntityContainerChangeHandler(channel, templateEngine));
      
      Object instance = entityPlan.newInstance();
      sessionData.entityModel.setValue(instance);
    }

  }  

}
