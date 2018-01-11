package org.gyfor.web.form;


import java.util.Collection;
import java.util.Map;

import org.gyfor.http.AbstractWebSocketConnectionCallback;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.http.WebSocketSession;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.ref.ObjectReference;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSocketChannel;


@Context("/roundtrip")
@Resource(path = "/resources", location = "resources")
@Component(service = HttpHandler.class)
public class RoundtripWebSocket extends WebSocketProtocolHandshakeHandler {

  @Reference
  private IModelFactory modelFactory;

  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  
  private RoundtripWebSocketConnectionCallback callback;
  
  
  public RoundtripWebSocket() {
    super (new RoundtripWebSocketConnectionCallback());
  }
  
  
  private static class RoundtripWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {
    
    private BundleContext bundleContext;
    
    private IModelFactory modelFactory;
    
    private ITemplateEngine templateEngine;


    
    public void setBundleContext (BundleContext bundleContext) {
      this.bundleContext = bundleContext;
    }
    
    
    public void setModelFactory (IModelFactory modelFactory) {
      this.modelFactory = modelFactory;
    }
    
    
    public void setTemplateEngine (ITemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
    }
    
    
    @Override
    protected Object buildSessionData(String path, Map<String, String> queryMap, WebSocketChannel channel) {
      System.out.println("Round trip: build session data");
      
      if (path == null || path.length() == 0) {
        throw new IllegalArgumentException("No object reference specified");
      }
      try {
        // Assuming the path starts with a slash (/)
        path = path.substring(1);
        Collection<ServiceReference<ObjectReference>> serviceRefs = bundleContext.getServiceReferences(ObjectReference.class, "(name=" + path + ")");
        if (serviceRefs.size() == 0) {
          throw new IllegalArgumentException("No object reference named '" + path + "' was found");
        }
        ServiceReference<ObjectReference> serviceRef = serviceRefs.iterator().next();
        ObjectReference objectRef = bundleContext.getService(serviceRef);
        String objectClassName = objectRef.getClassName();
        IEntityModel objectModel = modelFactory.buildEntityModel(objectClassName);
        
        boolean hasTitle = (queryMap.get("popup") == null); 
        TemplateModelListener eventListener = new TemplateModelListener(channel, templateEngine, hasTitle);
        objectModel.addEntityCreationListener(eventListener);
        objectModel.addContainerChangeListener(eventListener);
        objectModel.addItemEventListener(eventListener);
        //Object instanceValue = objectModel.newInstance();
        //objectModel.setValue(instanceValue);
        
        return objectModel;
      } catch (ClassNotFoundException ex) {
        throw new RuntimeException(ex);
      } catch (InvalidSyntaxException ex) {
        throw new RuntimeException(ex);
      }
    }


    @Override
    protected void doRequest(String command, String[] args, Object sessionData, WebSocketSession wss) {
      System.out.println("Round trip: doRequest " + command);
      switch (command) {
      case "hello" :
        IEntityModel objectModel = (IEntityModel)sessionData;
        Object instance = objectModel.newInstance();
        objectModel.setValue(instance);
        break;
      case "input" :
        IEntityModel objectModel2 = (IEntityModel)sessionData;
        int id = Integer.parseInt(args[0]);
        IItemModel item = objectModel2.getById(id);
        item.setValueFromSource(args[1]);
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
    
  }
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    callback = CallbackAccessor.getCallback(this);

    BundleContext context = componentContext.getBundleContext();
    callback.setBundleContext(context);
    callback.setModelFactory(modelFactory);
    
    ITemplateEngine templateEngine = templateEngineFactory.buildTemplateEngine(componentContext.getBundleContext());
    templateEngine.addTokenParser(new EntityTokenParser());
    templateEngine.addTokenParser(new FieldTokenParser());
    
    callback.setTemplateEngine(templateEngine);
  }
  
  
  @Deactivate 
  public void deactivate () {
    callback.closeAllSessions();
  }

}
