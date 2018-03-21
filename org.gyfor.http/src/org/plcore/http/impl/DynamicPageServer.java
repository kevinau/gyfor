package org.plcore.http.impl;

import java.nio.file.Paths;

import io.undertow.Undertow;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.util.Headers;


public class DynamicPageServer {

  public static void main(final String[] args) {
    HttpHandler defaultHandler = new HttpHandler() {
      @Override
      public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Default handler...");
      }
    };

    PathHandler pathHandler = new PathHandler(defaultHandler);
    Undertow server = Undertow.builder().addHttpListener(8088, "localhost").setHandler(pathHandler).build();
    server.start();

    pathHandler.addPrefixPath("/aaa", new HttpHandler() {
      @Override
      public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("/aaa handler...");
      }
    });
    
    @SuppressWarnings("unused")
    PathHandler ph = pathHandler.addPrefixPath("/bbb", new HttpHandler() {
      @Override
      public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("old /bbb handler...");
      }
    });
    ph = pathHandler.removePrefixPath("/bbb");
    
    HttpHandler noExtensionHandler = new HttpHandler() {
      @Override
      public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("/bbb no extension handler...");
      }
    };
    HttpHandler extensionHandler = new HttpHandler() {
      @Override
      public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("/bbb .css and .js handler...");
      }
    };
    PredicateHandler predicateHandler = new PredicateHandler(Predicates.suffixes(".css", ".js"), extensionHandler, noExtensionHandler);
    pathHandler.addPrefixPath("/bbb", predicateHandler);
    
    ResourceManager resourceManager = new PathResourceManager(Paths.get("static"), 1024 * 8);
    pathHandler.addPrefixPath("/static", new ResourceHandler(resourceManager));
  }
}
