package org.gyfor.web.form;


import java.util.Collection;
import java.util.Map;

import org.gyfor.http.AbstractWebSocketConnectionCallback;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.http.WebSocketSession;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.ref.ObjectReference;
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


@Context("/roundtrip")
@Resource(path = "/resources", location = "resources")
@Component(service = HttpHandler.class)
public class RoundtripWebSocket extends WebSocketProtocolHandshakeHandler {

  @Reference
  private IModelFactory modelFactory;
  
  private RoundtripWebSocketConnectionCallback callback;
  
  
  public RoundtripWebSocket() {
    super (new RoundtripWebSocketConnectionCallback());
  }
  
  
  private static class RoundtripWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {
    
    private BundleContext bundleContext;
    
    private IModelFactory modelFactory;
    
    
    public void setBundleContext (BundleContext bundleContext) {
      this.bundleContext = bundleContext;
    }
    
    
    public void setModelFactory (IModelFactory modelFactory) {
      this.modelFactory = modelFactory;
    }
    
    
    @Override
    protected Object buildSessionData(String path, Map<String, String> queryMap) {
      if (path == null || path.length() == 0) {
        throw new IllegalArgumentException("No object reference specified");
      }
      try {
        Collection<ServiceReference<ObjectReference>> serviceRefs = bundleContext.getServiceReferences(ObjectReference.class, "(name=" + path + ")");
        if (serviceRefs.size() == 0) {
          throw new IllegalArgumentException("No object reference named '" + path + "' was found");
        }
        ServiceReference<ObjectReference> serviceRef = serviceRefs.iterator().next();
        ObjectReference objectRef = bundleContext.getService(serviceRef);
        String objectClassName = objectRef.getClassName();
        IEntityModel objectModel = modelFactory.buildEntityModel(objectClassName);
        
        Object instanceValue = objectModel.newInstance();
        objectModel.setValue(instanceValue);
        
        return objectModel;
      } catch (ClassNotFoundException ex) {
        throw new RuntimeException(ex);
      } catch (InvalidSyntaxException ex) {
        throw new RuntimeException(ex);
      }
    }


    @Override
    protected void doRequest(String command, String[] args, Object sessionData, WebSocketSession wss) {
      switch (command) {
      case "hello" :
        IEntityModel objectModel = (IEntityModel)sessionData;
        objectModel.dump();
        break;
      case "input" :
        int id = Integer.parseInt(args[0]);
        try {
          int value = Integer.parseInt(args[1].trim());
          wss.sendText("clearError", id);
        } catch (NumberFormatException ex) {
          wss.sendText("setError", id, "Not a number");
        }
        System.out.println("....... " + args[0] + "=" + args[1]);
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
  }
  
  
  @Deactivate 
  public void deactivate () {
    //callback.stopTicking();
    callback.closeAllSessions();
  }
  
  
  //////////////////////////////////////////////////////////////
  
  //@Component(service=DialectRegistry.class, configurationPolicy=ConfigurationPolicy.IGNORE)
  //public class DialectRegistry {

//    public String[] getDialectNames () {
//      try {
//        Collection<ServiceReference<IDialect>> serviceRefs = context.getServiceReferences(IDialect.class, null);
//        String[] names = new String[serviceRefs.size()];
//        
//        int i = 0;
//        for (ServiceReference<IDialect> serviceRef : serviceRefs) {
//          names[i++] = (String)serviceRef.getProperty("dialectName");
//        }
//        Arrays.sort(names);
//        return names;
//      } catch (InvalidSyntaxException ex) {
//        throw new RuntimeException(ex);
//      }
//    }

}
