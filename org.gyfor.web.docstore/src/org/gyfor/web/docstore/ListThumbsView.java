package org.gyfor.web.docstore;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.docstore.Document;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.http.Context;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.SecondaryIndex;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/d/list")
@Component(service = HttpHandler.class, immediate = false, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ListThumbsView implements HttpHandler {

  private ITemplateEngine templateEngine;
  private ITemplate template = null;
  
  private IDocumentStore docStore;
  private ITemplateEngineFactory templateEngineFactory;

  @Reference
  public void setDocumentStore (IDocumentStore docStore) {
    this.docStore = docStore;
  }
  
  
  public void unsetDocumentStore (IDocumentStore docStore) {
    this.docStore = null;
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
    templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
  }

  
  @Deactivate
  public void deactivate() {
    this.templateEngine = null;
  }


  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    
//    String id = exchange.getRelativePath();
//    if (id == null || id.length() <= 1) {
//      HttpUtility.endWithStatus(exchange, 400, "Document id not specified as part of request");
//      return;
//    }
//    // Remove leading slash (/)
//    id = id.substring(1);
//    
//    Document document = docStore.getDocument(id);
//    if (document == null) {
//      HttpUtility.endWithStatus(exchange, 404, "No document found for '" + id + "'");
//      return;
//    }
    
    // Lazily create template
    if (template == null) {
      template = templateEngine.getTemplate("listThumbsView");
    }

    Map<String, Object> context = new HashMap<>();
    context.put("hostAndPort", exchange.getHostAndPort());
    context.put("context", exchange.getResolvedPath());

    List<String> idList = new ArrayList<>();
    SecondaryIndex<Date, String, Document> importDateIndex = docStore.getImportDateIndex();
    EntityCursor<Document> indexCursor = importDateIndex.entities();
    try {
      for (Document doc : indexCursor) {
        idList.add(doc.getId());
      }
    } finally {
      indexCursor.close();
    } 

    context.put("docStore", docStore);
    context.put("idList", idList);
    
    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");    
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, context);
  }

}
