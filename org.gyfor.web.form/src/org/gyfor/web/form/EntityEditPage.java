package org.gyfor.web.form;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.http.Context;
import org.gyfor.http.HttpUtility;
import org.gyfor.http.Resource;
import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.object.plan.ILabelGroup;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.impl.TemplateEngine;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/entity")
@Resource(path = "/resources", location = "resources")
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class EntityEditPage implements HttpHandler {

  private ITemplateEngine templateEngine;
  
  
  @Activate
  public void activate(BundleContext bundleContext) {
    templateEngine = new TemplateEngine(bundleContext);
  }

  
  @Deactivate
  public void deactivate() {
  }

  
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    String entityName = exchange.getRelativePath();
    if (entityName == null || entityName.length() == 0) {
      HttpUtility.endWithStatus(exchange, 400, "Entity name not specified as part of request");
      return;
    }
    entityName = entityName.substring(1);
    Class<?> entityClass = Class.forName(entityName);
    ILabelGroup labels = new EntityLabelGroup(entityClass);
    
    ITemplate template = templateEngine.getTemplate("entityEditPage");
  
    Map<String, Object> entityEditContext = new HashMap<>();
    entityEditContext.put("hostAndPort", exchange.getHostAndPort());
    entityEditContext.put("context", exchange.getResolvedPath());
    
    entityEditContext.put("entityName", entityName);
    entityEditContext.put("labels", labels);

    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, entityEditContext);
  }

}
