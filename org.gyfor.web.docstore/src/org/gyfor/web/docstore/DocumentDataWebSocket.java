package org.gyfor.web.docstore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gyfor.dao.EntityDescription;
import org.gyfor.doc.DocumentData;
import org.gyfor.doc.DocumentTypeRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.http.AbstractWebSocketConnectionCallback;
import org.plcore.http.CallbackAccessor;
import org.plcore.http.Context;
import org.plcore.http.Response;
import org.plcore.http.WebSocketSession;
import org.plcore.template.ITemplateEngine;
import org.plcore.template.ITemplateEngineFactory;
import org.plcore.web.global.JSEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;


////@Context("/ws/documentData")
////@Component(service = HttpHandler.class)
public class DocumentDataWebSocket extends WebSocketProtocolHandshakeHandler {

  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  private DocumentDataWebSocketConnectionCallback callback;
  
  
  public DocumentDataWebSocket() {
    super (new DocumentDataWebSocketConnectionCallback());
    callback = CallbackAccessor.getCallback(this);
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    ITemplateEngine templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    callback.setTemplateEngine(templateEngine);
  }
  
  
  @Deactivate 
  public void deactivate () {
    callback.closeAllSessions();
  }
  
  
  private static class DocumentDataWebSocketConnectionCallback extends AbstractWebSocketConnectionCallback {
    
    private Logger logger = LoggerFactory.getLogger(DocumentDataWebSocket.class);
    
    private Map<String, DocumentData> docDataMap = new ConcurrentHashMap<>();
    
    //private DocumentTypeRegistry docTypeRegistry;
    
    private ITemplateEngine templateEngine;
    //private ITemplate allDescriptionsTemplate;
    
    
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
    protected void onConnect(WebSocketHttpExchange exchange) {
      //exchange.
    }

    
    @Override
    protected void handleTextMessage(WebSocketSession session, String command, String data) {
      switch (command) {
      case "getDocumentType" :
        // Data consists of: hashCode | <nothing>
        int n = data.indexOf('|');
        String hashCode = data.substring(0, n);
        sendAllDescriptions(session, hashCode);
        break;
      default :
        throw new RuntimeException("Unknown command: '" + command + "'");
      }
    }
    
    
    @Override
    protected void onClose() {
    }

    @Override
    protected Object buildSessionData(String path, Map<String, String> queryMap) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    protected void doRequest(String command, String[] args, Object sessionData, WebSocketSession wss) {
      // TODO Auto-generated method stub
      
    }

    @Override
    protected void openResources() {
      // TODO Auto-generated method stub
      
    }

    @Override
    protected void closeResources() {
      // TODO Auto-generated method stub
      
    }
  }
  
}
