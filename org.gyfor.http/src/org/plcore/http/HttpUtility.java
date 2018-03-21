package org.plcore.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class HttpUtility {

  public static void endWithStatus (HttpServerExchange exchange, int statusCode, String message) {
    exchange.setStatusCode(statusCode);
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
    exchange.getResponseSender().send(statusCode + " " + message);
    exchange.endExchange();
  }

}
