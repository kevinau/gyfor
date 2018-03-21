package org.plcore.http.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.plcore.http.Context;
import org.plcore.http.IDynamicResourceLocation;
import org.plcore.http.PageNotFoundHandler;
import org.plcore.http.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PredicateHandler;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;


public class UndertowService implements BundleActivator {

  private Logger log = LoggerFactory.getLogger(UndertowService.class);

  private UndertowWebServer webServer;


  @Override
  public void start(BundleContext bundleContext) throws Exception {
    webServer = new UndertowWebServer("localhost", 8123);
    webServer.start();

    ServiceListener sl = new ServiceListener() {
      @Override
      public void serviceChanged(ServiceEvent ev) {
        ServiceReference<?> sr = ev.getServiceReference();
        switch (ev.getType()) {
        case ServiceEvent.REGISTERED: {
          try {
            register(bundleContext, sr);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        }
      }
    };

    String filter = "(objectclass=" + HttpHandler.class.getName() + ")";

    try {
      bundleContext.addServiceListener(sl, filter);
      Collection<ServiceReference<HttpHandler>> srl = bundleContext.getServiceReferences(HttpHandler.class, filter);
      if (srl != null) {
        for (ServiceReference<HttpHandler> sr : srl) {
          register(bundleContext, sr);
        }
      }
    } catch (InvalidSyntaxException e) {
      e.printStackTrace();
    }
  }


  private void register(BundleContext bundleContext, ServiceReference<?> sr) {
    Object service = bundleContext.getService(sr);
    if (service == null) {
      throw new IllegalArgumentException("Handler for dynamic web page, or static resource, is null (sr = " + sr + ")");
    }
    if (!(service instanceof HttpHandler)) {
      return;
    }
    HttpHandler handler = (HttpHandler)service;
    
    Map<String, HttpHandler> handlerSet = new HashMap<>();
    String context = "";

    Class<?> klass = handler.getClass();
    Context contextAnn = klass.getAnnotation(Context.class);
    if (contextAnn != null) {
      context = contextAnn.value();
      if (!context.startsWith("/")) {
        throw new IllegalArgumentException("@Context 'path' must start with a slash (/)");
      }
      handlerSet.put(context, handler);
    }

    Resource[] resourcesAnn = klass.getAnnotationsByType(Resource.class);
    for (Resource resourceAnn : resourcesAnn) {
      String[] extensions = resourceAnn.extensions();
      if (extensions.length == 0) {
        ResourceManager resourceManager;
        
        String subPath = resourceAnn.path();
        if (resourceAnn.dynamic()) {
          if (IDynamicResourceLocation.class.isInstance(handler)) {
            resourceManager = new DeferredPathResourceManager((IDynamicResourceLocation)handler);
            log.info("Adding resource: sub-path {}, extensions {}, at {}", subPath, extensions, handler.getClass().getCanonicalName());
          } else {
            throw new IllegalArgumentException("A handler with @Resource(dynamic=true) must implement IDynamicResourcePath");
          }
        } else {
          if (!subPath.startsWith("/")) {
            throw new IllegalArgumentException("@Resource 'path' must start with a slash (/)");
          }
          String resourceLocation = resourceAnn.location();
          log.info("Adding resource: sub-path {}, extensions {}, at {}", subPath, extensions, resourceLocation);
          resourceManager = new BundleResourceManager(sr.getBundle(), resourceLocation);
        }

        
        String resourceContext = context + subPath;
        HttpHandler nextHandler = handlerSet.get(resourceContext);
        if (nextHandler == null) {
          nextHandler = PageNotFoundHandler.instance;
        }
        HttpHandler resourceHandler = new ResourceHandler(resourceManager, nextHandler);
        handlerSet.put(resourceContext, resourceHandler);
      }
    }
    for (Resource resourceAnn : resourcesAnn) {
      String[] extensions = resourceAnn.extensions();
      if (extensions.length > 0) {
        String subPath = resourceAnn.path();
        String locationBase = resourceAnn.location();
        String resourceContext = context + subPath;
        log.info("Adding resource: sub-path {}, extensions {}, based at {}", subPath, extensions, locationBase);

        HttpHandler nextHandler = handlerSet.get(resourceContext);
        if (nextHandler == null) {
          nextHandler = PageNotFoundHandler.instance;
        }
        ResourceManager resourceManager = new BundleResourceManager(sr.getBundle(), locationBase);
        HttpHandler resourceHandler = new ResourceHandler(resourceManager, PageNotFoundHandler.instance);
        HttpHandler predicateHandler = new PredicateHandler(Predicates.suffixes(extensions), resourceHandler, nextHandler);
        handlerSet.put(resourceContext, predicateHandler);
      }
    }
    for (Map.Entry<String, HttpHandler> entry : handlerSet.entrySet()) {
      context = entry.getKey();
      handler = entry.getValue();
      log.info("Registering: {} using {}", context, handler);
      webServer.register(context, handler);
    }
  }


  private void unregister(BundleContext bundleContext, ServiceReference<?> sr) {
    HttpHandler handler = (HttpHandler)bundleContext.getService(sr);
    
    Set<String> handlerSet = new HashSet<>();
    String context = "";

    Class<?> klass = handler.getClass();
    Context contextAnn = klass.getAnnotation(Context.class);
    if (contextAnn != null) {
      context = contextAnn.value();
      handlerSet.add(context);
    }

    Resource[] resourcesAnn = klass.getAnnotationsByType(Resource.class);
    for (Resource resourceAnn : resourcesAnn) {
      String subPath = resourceAnn.path();
      String resourceContext = context + subPath;
      handlerSet.add(resourceContext);
    }
    for (String cx : handlerSet) {
      log.info("Unr-rgistering: {} using {}", cx);
      webServer.unregister(cx);
    }
  }


  @Override
  public void stop(BundleContext bundleContext) throws Exception {
    String filter = "(objectclass=" + HttpHandler.class.getName() + ")";

    ServiceReference<?>[] srl = bundleContext.getServiceReferences((String)null, filter);
    for (int i = 0; srl != null && i < srl.length; i++) {
      unregister(bundleContext, srl[i]);
    }
    webServer.stop();
  }

}
