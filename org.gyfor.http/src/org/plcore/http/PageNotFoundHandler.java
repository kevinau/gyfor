package org.plcore.http;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class PageNotFoundHandler implements HttpHandler {

  public static PageNotFoundHandler instance = new PageNotFoundHandler();
  
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    exchange.setStatusCode(404);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
    exchange.getResponseSender().send("404 Page not found");
    exchange.endExchange();
  }

}
