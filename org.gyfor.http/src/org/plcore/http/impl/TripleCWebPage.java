package org.plcore.http.impl;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


//@Context("/ccc")
//@Component(immediate=true)
public class TripleCWebPage implements HttpHandler {

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
    exchange.getResponseSender().send("/ccc handler...");
  }

}
