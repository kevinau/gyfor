package org.plcore.http.impl;

import org.plcore.http.PageNotFoundHandler;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;


public class UndertowWebServer {

  private final String host;
  private final int port;
  
  private PathHandler pathHandler;
  private Undertow server;
  
  UndertowWebServer (String host, int port) {
    this.host = host;
    this.port = port;
    
    pathHandler = new PathHandler(PageNotFoundHandler.instance);
  }
  
  
  void register (String urlPath, HttpHandler handler) {
    pathHandler.addPrefixPath(urlPath, handler);
  }
  
  
  void unregister (String urlPath) {
    pathHandler.removePrefixPath(urlPath);
  }
  
  
  void start () {
    server = Undertow.builder().addHttpListener(port, host).setHandler(pathHandler).build();
    server.start();
  }
  
  
  void stop () {
    server.stop();
  }
  
}
