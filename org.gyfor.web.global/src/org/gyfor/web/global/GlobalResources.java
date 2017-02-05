package org.gyfor.web.global;

import java.nio.file.Path;

import org.gyfor.http.IDynamicResourceLocation;
import org.gyfor.http.Resource;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;


@Resource(path = "/global", dynamic = true)
@Component(service = HttpHandler.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class GlobalResources implements HttpHandler, IDynamicResourceLocation  {

  private Logger logger = LoggerFactory.getLogger(GlobalResources.class);
  
  @Configurable
  private Path resourceDir;
  
  
  @Activate
  public void activate(ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    logger.info("Activating global resources with resource dir {}", resourceDir);
  }


  @Deactivate
  public void deactivate() {
  }


  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
  }


  @Override
  public Path getResourceLocation() {
    return resourceDir;
  }

}
