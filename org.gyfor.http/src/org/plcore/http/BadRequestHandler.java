package org.plcore.http;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class BadRequestHandler implements HttpHandler {

  public static BadRequestHandler instance = new BadRequestHandler();
  
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    exchange.setStatusCode(400);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
    exchange.getResponseSender().send("400 Bad request");
    exchange.endExchange();
  }

}
