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
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/ee")
@Resource(path = "/resources", location = "resources")
@Component(service=HttpHandler.class)
public class EntityEditPage implements HttpHandler {

  private Logger logger = LoggerFactory.getLogger(EntityEditPage.class);
  
  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  private ITemplateEngine templateEngine;
  
  
  @Activate
  public void activate(BundleContext bundleContext) {
    logger.info("Activating");
    templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    
    TokenParser entityTokenParser = new EntityTokenParser();
    templateEngine.addTokenParser(entityTokenParser);
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
    logger.info("handleRequest: {}", exchange.getRequestURL()
        );
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
    labels.loadContext(entityEditContext);

    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, entityEditContext);
  }

}
