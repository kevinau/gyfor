package org.gyfor.web.dao;

import java.util.List;
import java.util.Map;

import org.gyfor.dao.EntitySetRegistry;
import org.gyfor.dao.IEntitySet;
import org.gyfor.http.AbstractWebSocketConnectionCallback;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.http.Response;
import org.gyfor.http.WebSocketSession;
import org.gyfor.object.value.EntityDescription;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.gyfor.web.global.JSEdit;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;


@Context("/ws/entitySet")
@Component(service = HttpHandler.class)
public class EntitySetWebSocket extends WebSocketProtocolHandshakeHandler {

  @Reference
  private EntitySetRegistry entitySetRegistry;
  
  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  private EntitySetWebSocketConnectionCallback callback;
  
  
  public EntitySetWebSocket() {
    super (new EntitySetWebSocketConnectionCallback());
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    callback = CallbackAccessor.getCallback(this);
    callback.setEntitySetRegistry(entitySetRegistry);
    
    ITemplateEngine templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    callback.setTemplateEngine(templateEngine);
  }
  
  
  @Deactivate 
  public void deactivate () {
    callback.closeAllSessions();
  }
  
  
  private static class EntitySetWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {
    
    private Logger logger = LoggerFactory.getLogger(EntitySetWebSocket.class);
    
    private EntitySetRegistry entitySetRegistry;
    
    private ITemplateEngine templateEngine;
    private ITemplate allDescriptionsTemplate;
    
    
    private void setEntitySetRegistry(EntitySetRegistry entitySetRegistry) {
      this.entitySetRegistry = entitySetRegistry;
    }
    
    private void setTemplateEngine(ITemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
    }
    
    public void sendAllDescriptions (WebSocketSession session, String target, String entityName) {
      logger.info("sendAllDescriptions for {}, to JS target {}", entityName, target);
      
      IEntitySet entitySet = entitySetRegistry.getEntitySet(entityName);
      List<EntityDescription> descriptions = entitySet.getAllDescriptions();
      
      // Lazily create template
      if (allDescriptionsTemplate == null) {
        allDescriptionsTemplate = templateEngine.getTemplate("descriptionList");
      }
      allDescriptionsTemplate.putContext("descriptions", descriptions);
      String html = allDescriptionsTemplate.evaluate();
      
      Response response = new Response(JSEdit.REPLACE_CHILDREN.command(), target, html);
      session.sendText(response.toString());
      logger.info("{} descriptions sent", descriptions.size());
    }
    
    
    @Override
    protected Object buildSessionData(String path, Map<String, String> queryMap) {
      return null;
    }

    
    @Override
    protected void doRequest(String command, String[] args, Object sessionData, WebSocketSession wss) {
      switch (command) {
      case "getAllDescriptions" :
        // Data consists of: target | entityName
        String target = args[0];
        String entityName = args[1];
        sendAllDescriptions(wss, target, entityName);
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
  
}
