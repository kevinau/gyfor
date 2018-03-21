package org.plcore.http.impl;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

//@Context("/ddd")
//@Resource(extensions={".js"}, location="static")
//@Component(immediate=true)
public class TripleDReource implements HttpHandler {

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    //ResponseCodeHandler.HANDLE_404.handleRequest(exchange);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
    exchange.getResponseSender().send("/ddd handler...");
  }

}
