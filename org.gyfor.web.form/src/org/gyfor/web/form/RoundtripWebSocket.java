package org.gyfor.web.form;


import java.util.Collection;
import java.util.Map;

import org.gyfor.http.AbstractWebSocketConnectionCallback;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.http.WebSocketSession;
import org.gyfor.object.ref.IObjectReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


@Context("/roundtrip")
@Resource(path = "/resources", location = "resources")
@Component(service = HttpHandler.class)
public class RoundtripWebSocket extends WebSocketProtocolHandshakeHandler {

  private BundleContext context;
  
  private RoundtripWebSocketConnectionCallback callback;
  
  
  public RoundtripWebSocket() {
    super (new RoundtripWebSocketConnectionCallback());
  }
  
  
  private static class RoundtripWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {
    
    @Override
    protected Object buildSessionData(String path, Map<String, String> queryMap) {
      
      return null;
    }


    @Override
    protected void doRequest(String command, String[] args, Object sessionData, WebSocketSession wss) {
      switch (command) {
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
    context = componentContext.getBundleContext();
    callback = CallbackAccessor.getCallback(this);
    //callback.startTicking();
  }
  
  
  @Deactivate 
  public void deactivate () {
    //callback.stopTicking();
    callback.closeAllSessions();
    context = null;
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
    
    
  public IObjectReference getObjectReference (String name) {
    try {
      Collection<ServiceReference<IObjectReference>> serviceRefs = context.getServiceReferences(IObjectReference.class, "(name=" + name + ")");
      if (serviceRefs.size() != 1) {
        throw new IllegalArgumentException(name);
      }
      ServiceReference<IObjectReference> serviceRef = serviceRefs.iterator().next();
      return context.getService(serviceRef);
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

}
