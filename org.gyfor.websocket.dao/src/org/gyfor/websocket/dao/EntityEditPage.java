package org.gyfor.websocket.dao;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.dao.IDataAccessObject;
import org.gyfor.http.Context;
import org.gyfor.http.HttpUtility;
import org.gyfor.http.Resource;
import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.ILabelGroup;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/entity")
@Resource(path = "/resources", location = "resources")
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class EntityEditPage implements HttpHandler {

  private BundleContext bundleContext;
  
  private ITemplateEngineFactory templateEngineFactory;

  private ITemplateEngine templateEngine;
  
  
  @Reference
  public void setTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = templateEngineFactory;
  }
  
  
  public void unsetTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = null;
  }
  
  
  @Activate
  public void activate(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
    templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
  }

  
  @Deactivate
  public void deactivate() {
    this.bundleContext = null;
  }

  
  @SuppressWarnings("rawtypes")
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    String path = exchange.getRelativePath();
    if (path == null || path.length() == 0) {
      HttpUtility.endWithStatus(exchange, 400, "Entity name not specified as part of request");
      return;
    }
    // Remove leading slash
    path = path.substring(1);

    IDataAccessObject dao;
    try {
      Collection<ServiceReference<IDataAccessObject>> refs = bundleContext.getServiceReferences(IDataAccessObject.class, "(name=" + path + ")");
      if (refs.size() != 1) {
        throw new IllegalArgumentException("Expecting one IDataAccessObject with (name=" + path + "), found " + refs.size());
      }
      ServiceReference<IDataAccessObject> ref = refs.iterator().next();
      dao = bundleContext.getService(ref);
    } catch (InvalidSyntaxException ex) {
      throw new RuntimeException(ex);
    }

    IEntityPlan<?> entityPlan = dao.getEntityPlan();
    Class<?> entityClass = entityPlan.getSourceClass();
    ILabelGroup labels = new EntityLabelGroup(entityClass);
    
    ITemplate template = templateEngine.getTemplate("EntityEditPage");
  
    Map<String, Object> entityEditContext = new HashMap<>();
    entityEditContext.put("hostAndPort", exchange.getHostAndPort());
    entityEditContext.put("context", exchange.getResolvedPath());
    
    entityEditContext.put("entityName", path);
    entityEditContext.put("labels", labels);
    entityEditContext.put("descriptionListId", "descriptionsList");


    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, entityEditContext);
  }

}
