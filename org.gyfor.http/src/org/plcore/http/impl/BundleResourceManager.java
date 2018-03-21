package org.plcore.http.impl;

import java.io.IOException;
import java.net.URL;

import org.osgi.framework.Bundle;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;


/**
 * A ResourceManager that references resources in an OSGi bundle.
 */
public class BundleResourceManager implements ResourceManager {

  /**
   * The bundle that is used to load resources
   */
  private final Bundle bundle;
  /**
   * The prefix that is appended to resources that are to be loaded.
   */
  private final String resourcePath;


  public BundleResourceManager(final Bundle bundle, final String resourcePath) {
    this.bundle = bundle;
    if (resourcePath.equals("")) {
      this.resourcePath = "";
    } else if (resourcePath.endsWith("/")) {
      this.resourcePath = resourcePath;
    } else {
      this.resourcePath = resourcePath + "/";
    }
  }


  public BundleResourceManager(final Bundle bundle) {
    this(bundle, "");
  }


  @Override
  public Resource getResource(final String path) throws IOException {
    String modPath = path;
    if (modPath.startsWith("/")) {
      modPath = path.substring(1);
    }
    
    URL resource = bundle.getResource(resourcePath + modPath);
    if (resource == null) {
      return null;
    } else {
      return new URLResource(resource, resource.openConnection(), path);
    }

  }


  @Override
  public boolean isResourceChangeListenerSupported() {
    return false;
  }


  @Override
  public void registerResourceChangeListener(ResourceChangeListener listener) {
    throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
  }


  @Override
  public void removeResourceChangeListener(ResourceChangeListener listener) {
    throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
  }


  @Override
  public void close() throws IOException {
  }
}
