package org.gyfor.web.docstore;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.docstore.Document;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.ISegment;
import org.gyfor.docstore.Party;
import org.gyfor.docstore.SegmentType;
import org.gyfor.http.Context;
import org.gyfor.http.HttpUtility;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/d")
@Component(service = HttpHandler.class, immediate = false, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class SingleDocumentView implements HttpHandler {

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
    
    String id = exchange.getRelativePath();
    if (id == null || id.length() <= 1) {
      HttpUtility.endWithStatus(exchange, 400, "Document id not specified as part of request");
      return;
    }
    // Remove leading slash (/)
    id = id.substring(1);
    
    Document doc = docStore.getDocument(id);
    if (doc == null) {
      HttpUtility.endWithStatus(exchange, 404, "No document found for '" + id + "'");
      return;
    }
    
    // Lazily create template
    if (template == null) {
      template = templateEngine.getTemplate("singleDocumentView");
    }

    Map<String, Object> context = new HashMap<>();
    context.put("hostAndPort", exchange.getHostAndPort());
    context.put("context", exchange.getResolvedPath());
    context.put("docStore", docStore);
    
    ISegment companySegment = doc.getContents().getUniqueSegment(SegmentType.COMPANY_NUMBER);
    if (companySegment != null) {
      int partyId = 1;
      String shortName = "Qantas";
      String formalName = "Qantas Airways";
      String webPage = "www.qantas.com";
      Party party = new Party(partyId, (String)companySegment.getValue(), shortName, formalName, webPage);
      context.put("party", party);
    }
//    List<Company> companies = new ArrayList<>();
//    boolean cleanCompanyList = true;
//    for (ISegment seg : doc.getContents().getSegments()) {
//      if (seg.getType() == SegmentType.COMPANY_NUMBER) {
//        Company company = companyService.getCompany((String)seg.getValue());
//        if (company == null) {
//          cleanCompanyList = false;
//        } else {
//          companies.add(company);
//        }
//      }
//    }
    
    int pages = doc.getContents().getPageCount();
    List<String> imagePaths = new ArrayList<>(pages);
    for (int i = 0; i < pages; i++) {
      imagePaths.add(docStore.webViewImagePath(doc.getId(), doc.getOriginExtension(), i));
    }
    context.put("imagePaths", imagePaths);
    context.put("pageImages", doc.getContents().getPageImages());
    context.put("imageScale", IDocumentStore.IMAGE_SCALE);
    context.put("sourcePath", docStore.webSourcePath(doc));
    context.put("document", doc);
    context.put("segments", doc.getContents().getSegments());
    
    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");    
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, context);
  }

}
