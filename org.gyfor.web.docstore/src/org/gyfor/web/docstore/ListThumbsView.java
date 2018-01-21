package org.gyfor.web.docstore;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.doc.DocumentSummary;
import org.gyfor.doc.IDocumentStore;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/d/list")
@Component(service = HttpHandler.class, immediate = false, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ListThumbsView implements HttpHandler {

  private static Logger logger = LoggerFactory.getLogger(ListThumbsView.class);

  private ITemplateEngine templateEngine;
  private ITemplate template = null;

  @Reference
  private IDocumentStore docStore;

  @Reference
  private ITemplateEngineFactory templateEngineFactory;

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

    // String id = exchange.getRelativePath();
    // if (id == null || id.length() <= 1) {
    // HttpUtility.endWithStatus(exchange, 400, "Document id not specified as
    // part of request");
    // return;
    // }
    // // Remove leading slash (/)
    // id = id.substring(1);
    //
    // Document document = docStore.getDocument(id);
    // if (document == null) {
    // HttpUtility.endWithStatus(exchange, 404, "No document found for '" + id +
    // "'");
    // return;
    // }

    // Lazily create template
    if (template == null) {
      template = templateEngine.getTemplate("listThumbsView");
    }

    Map<String, Object> context = new HashMap<>();

    List<DocumentSummary> docList = docStore.getAllDocuments();
    Collections.sort(docList, new Comparator<DocumentSummary>() {

      @Override
      public int compare(DocumentSummary arg0, DocumentSummary arg1) {
        return arg0.getImportTime().compareTo(arg1.getImportTime());
      }
    });

    context.put("docStore", docStore);
    context.put("docList", docList);

    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, context);
  }

}
