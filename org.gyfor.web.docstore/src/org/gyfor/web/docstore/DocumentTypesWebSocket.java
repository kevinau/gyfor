package org.gyfor.web.docstore;

import java.util.List;
import java.util.Map;

import org.gyfor.http.AbstractWebSocketConnectionCallback;
import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
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


////@Context("/ws/documentTypes")
////@Component(service = HttpHandler.class)
public class DocumentTypesWebSocket extends WebSocketProtocolHandshakeHandler {

  @Reference
  private DocumentTypeRegistry docTypeRegistry;
  
  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  private DocumentTypesWebSocketConnectionCallback callback;
  
  
  public DocumentTypesWebSocket() {
    super (new DocumentTypesWebSocketConnectionCallback());
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    callback = CallbackAccessor.getCallback(this);
    callback.setDocumentTypeRegistry(docTypeRegistry);
    
    ITemplateEngine templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    callback.setTemplateEngine(templateEngine);
  }
  
  
  @Deactivate 
  public void deactivate () {
    callback.closeAllSessions();
  }
  
  
  private static class DocumentTypesWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {
    
    private Logger logger = LoggerFactory.getLogger(DocumentTypesWebSocket.class);
    
    private DocumentTypeRegistry docTypeRegistry;
    
    private ITemplateEngine templateEngine;
    private ITemplate allDescriptionsTemplate;
    
    
    private void setDocumentTypeRegistry(DocumentTypeRegistry docTypeRegistry) {
      this.docTypeRegistry = docTypeRegistry;
    }
    
    private void setTemplateEngine(ITemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
    }
    
    public void sendAllDescriptions (WebSocketSession session, String target) {
      logger.info("sendAllDescriptions to JS target {}", target);
      
      List<EntityDescription> descriptions = docTypeRegistry.getAllDescriptions();
      
      // Lazily create template
      if (allDescriptionsTemplate == null) {
        allDescriptionsTemplate = templateEngine.getTemplate("descriptionList");
      }
      allDescriptionsTemplate.putContext("descriptions", descriptions);
      String html = allDescriptionsTemplate.evaluate();
      
      Response response = new Response(JSEdit.REPLACE_CHILDREN.command(), target, html);
      session.send(response.toString());
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
        // Data consists of: target | <nothing>
        String target = args[0];
        sendAllDescriptions(wss, target);
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
