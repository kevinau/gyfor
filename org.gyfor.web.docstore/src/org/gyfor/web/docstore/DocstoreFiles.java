package org.gyfor.web.docstore;

import java.nio.file.Path;

import org.gyfor.doc.IDocumentStore;
import org.gyfor.http.HttpUtility;
import org.gyfor.http.IDynamicResourceLocation;
import org.gyfor.http.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

@Resource(path = "/docstore", dynamic = true)
@Component(service = HttpHandler.class, immediate = true)
public class DocstoreFiles implements HttpHandler, IDynamicResourceLocation {

  @Reference
  private IDocumentStore docStore;

  
  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    HttpUtility.endWithStatus(exchange, 404, "No document found");
  }

  @Override
  public Path getResourceLocation() {
    return docStore.getBasePath();
  }

}
