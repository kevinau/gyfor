package org.gyfor.web.form;


import java.util.Collection;
import java.util.Map;

import org.gyfor.formref.FormReference;
import org.gyfor.web.form.state.IStateMachineFactory;
import org.gyfor.web.form.state.StateMachine;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.http.AbstractWebSocketConnectionCallback;
import org.plcore.http.CallbackAccessor;
import org.plcore.http.Context;
import org.plcore.http.ISessionData;
import org.plcore.http.Resource;
import org.plcore.http.WebSocketSession;
import org.plcore.template.ITemplateEngine;
import org.plcore.template.ITemplateEngineFactory;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.IModelFactory;

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
  
  
  private static class RoundtripWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback<RoundtripSessionData> {
    
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
    protected RoundtripSessionData buildSessionData(String path, Map<String, String> queryMap, WebSocketChannel channel) {
      System.out.println("Round trip: build session data");
      
      if (path == null || path.length() == 0) {
        throw new IllegalArgumentException("No object reference specified");
      }
      try {
        // Assuming the path starts with a slash (/)
        path = path.substring(1);
        Collection<ServiceReference<FormReference>> serviceRefs = bundleContext.getServiceReferences(FormReference.class, "(name=" + path + ")");
        if (serviceRefs.size() == 0) {
          throw new IllegalArgumentException("No object reference named '" + path + "' was found");
        }
        ServiceReference<FormReference> serviceRef = serviceRefs.iterator().next();
        FormReference formRef = bundleContext.getService(serviceRef);
        
        // Set up object model...
        String objectClassName = formRef.getEntityClassName();
        IEntityModel objectModel = modelFactory.buildEntityModel(objectClassName);
        
        boolean hasTitle = (queryMap.get("popup") == null); 
        TemplateModelListener eventListener = new TemplateModelListener(channel, templateEngine, hasTitle);
        objectModel.addEntityCreationListener(eventListener);
        objectModel.addContainerChangeListener(eventListener);
        objectModel.addItemEventListener(eventListener);
        objectModel.addEffectiveEntryModeListener(eventListener);
        //Object instanceValue = objectModel.newInstance();
        //objectModel.setValue(instanceValue);
        
        // ... and state machine, but do not fire any events yet
        String stateMachineFactoryName = formRef.getStateMachineFactoryClassName();
        IStateMachineFactory stateMachineFactory = (IStateMachineFactory)Class.forName(stateMachineFactoryName).newInstance(); 
        StateMachine<?,?> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.addActionChangeListener(eventListener);
        
        return new RoundtripSessionData(objectModel, stateMachine);
      } catch (ClassNotFoundException | InvalidSyntaxException | InstantiationException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
    }


    @Override
    protected void doRequest(String command, String[] args, ISessionData sessionData, WebSocketSession wss) {
      System.out.println("Round trip: doRequest " + command);
      switch (command) {
      case "hello" :
        //IEntityModel objectModel = ((RoundtripData)sessionData).entityModel();
        //Object instance = objectModel.newInstance();
        //objectModel.setValue(instance);
        //StateMachine stateMachine = ((RoundtripData)sessionData).stateMachine();
        //stateMachine.start(objectModel);
        sessionData.startSession();
        break;
      case "input" :
        IEntityModel objectModel2 = ((RoundtripSessionData)sessionData).entityModel();
        int id2 = Integer.parseInt(args[0]);
        IItemModel item2 = objectModel2.getById(id2);
        item2.setValueFromSource(args[1]);
        break;
      case "click" :
        IEntityModel objectModel3 = ((RoundtripSessionData)sessionData).entityModel();
        StateMachine<?,?> stateMachine3 = ((RoundtripSessionData)sessionData).stateMachine();
        stateMachine3.setAction(args[0], objectModel3);
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
