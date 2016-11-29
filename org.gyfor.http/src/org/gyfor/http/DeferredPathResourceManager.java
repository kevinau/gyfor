package org.gyfor.http;

import java.nio.file.Path;

import org.gyfor.http.api.IDynamicResourceLocation;

import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.Resource;

public class DeferredPathResourceManager extends PathResourceManager {

  private static final long transferMinSize = 4096;
  private static final boolean caseSensitive = true;
  private static final boolean followLinks = false;
  
  private final IDynamicResourceLocation dynamicLocation;
  
  
  protected DeferredPathResourceManager(IDynamicResourceLocation dynamicLocation) {
    super(transferMinSize, caseSensitive, followLinks);
    this.dynamicLocation = dynamicLocation;
  }
  
  
  @Override
  public Resource getResource(final String name) {
    Path basePath = super.getBasePath();
    if (basePath == null) {
      basePath = dynamicLocation.getPath();
      super.setBase(basePath);
    }
    return super.getResource(name);
  }

}
