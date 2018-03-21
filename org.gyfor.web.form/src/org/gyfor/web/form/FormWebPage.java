package org.gyfor.web.form;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.formref.FormReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.http.BadRequestHandler;
import org.plcore.http.Context;
import org.plcore.http.PageNotFoundHandler;
import org.plcore.http.Resource;
import org.plcore.template.ITemplate;
import org.plcore.template.ITemplateEngine;
import org.plcore.template.ITemplateEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/form")
@Resource(path = "/resources", location = "resources")
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class FormWebPage implements HttpHandler {

  private final Logger logger = LoggerFactory.getLogger(FormWebPage.class);

  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  private ITemplateEngine templateEngine;
  private BundleContext bundleContext;
  
  
  @Activate
  public void activate(BundleContext bundleContext) {
    this.templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    this.bundleContext = bundleContext;
  }

  
  @Deactivate
  public void deactivate() {
    this.templateEngine = null;
    this.bundleContext = null;
  }


  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    
    String uri = exchange.getRequestURI();
    logger.info("FormWebPage handle request: {}", uri);
    
    String context = "/form";
    if (!uri.startsWith(context)) {
      throw new RuntimeException("uri '" + uri + "' should start with " + context);
    }
    
    String requestPath = uri.substring(context.length());
    int n = requestPath.indexOf('?');
    if (n >= 0) {
      requestPath = requestPath.substring(0, n);
    }
    
    if (requestPath == null || requestPath.length() == 0) {
      exchange.dispatch(BadRequestHandler.instance);
      return;
      //throw new IllegalArgumentException("No form name specified");
    }
    
    // Assuming the path starts with a slash (/)
    String formName = requestPath.substring(1);
    Collection<ServiceReference<FormReference>> serviceRefs = bundleContext.getServiceReferences(FormReference.class, "(name=" + formName + ")");
    if (serviceRefs.size() == 0) {
      logger.info("No " + FormReference.class + " service ref for (name = " + formName + ")");
      exchange.dispatch(PageNotFoundHandler.instance);
      return;
      //throw new IllegalArgumentException("No form reference named '" + requestPath + "' was found");
    }
    ServiceReference<FormReference> serviceRef = serviceRefs.iterator().next();
    FormReference formRef = bundleContext.getService(serviceRef);
    String formClassName = formRef.getEntityClassName();
    
    //String templateName = formClassName + "(entityEditPage)";
    ITemplate template = templateEngine.getTemplate("entityEditPage");

    Map<String, Object> siteContext = new HashMap<>();
    siteContext.put("name", formName);
    siteContext.put("className", formClassName);
    
    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");    
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, siteContext);
  }

}
