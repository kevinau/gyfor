package org.plcore.http.impl;

import java.nio.file.Path;

import org.plcore.http.IDynamicResourceLocation;

import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.Resource;

public class DeferredPathResourceManager extends PathResourceManager {

  private static final long transferMinSize = 4096;
  private static final boolean caseSensitive = true;
  private static final boolean followLinks = false;
  
  private final IDynamicResourceLocation dynamicLocation;
  private boolean basePathSet = false;
  
  
  protected DeferredPathResourceManager(IDynamicResourceLocation dynamicLocation) {
    super(transferMinSize, caseSensitive, followLinks);
    this.dynamicLocation = dynamicLocation;
  }
  
  
  @Override
  public Resource getResource(final String name) {
    if (!basePathSet) {
      Path basePath = dynamicLocation.getResourceLocation();
      super.setBase(basePath);
      basePathSet = true;
    }
    return super.getResource(name);
  }

}
