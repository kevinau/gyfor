package org.plcore.web.about;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.plcore.http.Context;
import org.plcore.http.Resource;
import org.plcore.template.ITemplate;
import org.plcore.template.ITemplateEngine;
import org.plcore.template.ITemplateEngineFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/about")
@Resource(path = "/resources", location = "resources")
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class AboutServlet implements HttpHandler {

  private ITemplateEngineFactory templateEngineFactory;
  private ITemplateEngine templateEngine;
  private ITemplate template = null;
  
  
  @Reference (cardinality=ReferenceCardinality.MANDATORY)
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
    
    // Lazily create template
    if (template == null) {
      template = templateEngine.getTemplate("about");
    }

    Map<String, Object> siteContext = new HashMap<>();
    siteContext.put("hostAndPort", exchange.getHostAndPort());
    siteContext.put("context", exchange.getResolvedPath());
    
    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");    
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, siteContext);
  }

}
