package org.gyfor.http;

import org.gyfor.http.api.Context;
import org.gyfor.http.api.Resource;
import org.osgi.service.component.annotations.Component;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

//Context("/eee")
//@Resource(path="/static", location="static")
//@Resource(extensions={".css"}, location="static")
//@Component(immediate=true)
public class TripleEResource implements HttpHandler {

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    //ResponseCodeHandler.HANDLE_404.handleRequest(exchange);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
    exchange.getResponseSender().send("/eee handler...");
  }

}
