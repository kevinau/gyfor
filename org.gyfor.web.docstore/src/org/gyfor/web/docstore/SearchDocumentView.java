package org.gyfor.web.docstore;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.search.DocumentReference;
import org.gyfor.docstore.search.ISearchEngine;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/d/search")
@Resource(path = "/resources", location = "resources")
@Component(service = HttpHandler.class, immediate = false, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class SearchDocumentView implements HttpHandler {

  private static final String TEMPLATE_NAME = "searchDocumentView";
  
  private static Logger logger = LoggerFactory.getLogger(SearchDocumentView.class);

  private ITemplateEngine templateEngine;
  private ITemplate template = null;
  
  
  @Reference
  private IDocumentStore docStore;
  
  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  @Reference
  private ISearchEngine searchEngine;

  
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
    logger.info("Handle request: {}", exchange.getRequestPath());
    
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
      template = templateEngine.getTemplate(TEMPLATE_NAME);
    }

    Map<String, Object> context = new HashMap<>();
    context.put("hostAndPort", exchange.getHostAndPort());
    context.put("context", exchange.getResolvedPath());
    context.put("docStore", docStore);

    Map<String, Deque<String>> params = exchange.getQueryParameters();
    Deque<String> queryList = params.get("q");
    if (queryList == null || queryList.isEmpty()) {
      context.put("qValue", "");
      context.put("noQuery", true);
    } else {
      String queryString = queryList.getFirst();
      if (queryString.length() == 0) {
        context.put("qValue", "");
        context.put("noQuery", true);
      } else {
        context.put("qValue", queryString);
        
        List<DocumentReference> found = searchEngine.searchIndex(queryString);
        Collections.sort(found);
      
        Thumb[] thumbs = new Thumb[found.size()];
        int i = 0;
        for (DocumentReference docRef : found) {
          thumbs[i] = new Thumb(docRef.getDocumentId(), docRef.getDate());
          i++;
        }
    
        ThumbGroupBuilder groupBuilder = new ThumbGroupBuilder();
        List<ThumbGroup> resultGroups = groupBuilder.buildThumbGroups(thumbs);
        context.put("resultGroups", resultGroups);
      }
    }
    
    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");    
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, context);
  }

}
