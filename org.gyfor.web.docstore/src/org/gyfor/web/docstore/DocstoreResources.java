package org.gyfor.web.docstore;

import java.nio.file.Path;

import org.gyfor.docstore.IDocumentStore;
import org.gyfor.http.Context;
import org.gyfor.http.HttpUtility;
import org.gyfor.http.IDynamicResourceLocation;
import org.gyfor.http.Resource;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;


@Context("/d")
@Resource(path = "/docstore", dynamic = true)
@Component(service = HttpHandler.class, immediate = false, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class DocstoreResources implements HttpHandler, IDynamicResourceLocation {

  private IDocumentStore docStore;

  @Reference
  public void setDocumentStore (IDocumentStore docStore) {
    this.docStore = docStore;
  }
  
  
  public void unsetDocumentStore (IDocumentStore docStore) {
    this.docStore = null;
  }
  
  
  @Activate
  public void activate(BundleContext bundleContext) {
  }

  
  @Deactivate
  public void deactivate() {
  }


  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    HttpUtility.endWithStatus(exchange, 404, "No document found");
  }


  @Override
  public Path getResourceLocation() {
    return docStore.getBasePath();
  }

}
