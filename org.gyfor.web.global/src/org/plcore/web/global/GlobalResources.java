package org.plcore.web.global;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.plcore.http.Resource;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;


@Resource(path = "/global/fonts", location="resources/fonts")
@Resource(path = "/global", location="resources")
@Component(service = HttpHandler.class, immediate = true)
public class GlobalResources implements HttpHandler {

  @Activate
  public void activate(ComponentContext componentContext) {
  }


  @Deactivate
  public void deactivate() {
  }


  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
  }

}
