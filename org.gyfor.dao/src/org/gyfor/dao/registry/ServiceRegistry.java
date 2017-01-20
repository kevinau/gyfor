package org.gyfor.dao.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.gyfor.dao.IServiceRegistry;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class ServiceRegistry<S> implements IServiceRegistry<S> {

  private final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
  
  
  private Map<String, S> services = new HashMap<>();
  
  private static class Callback<T> {
    private String className;
    private Consumer<T> consumer;
 
    private Callback (String className, Consumer<T> consumer) {
      this.className = className;
      this.consumer = consumer;
    }
  }
  
  private List<Callback<S>> onRegisterCallbacks = new ArrayList<>();
  private List<Callback<S>> onUnregisterCallbacks = new ArrayList<>();
  
  
  @Override
  public void register (String className, S service) {
    synchronized (services) {
      if (services.containsKey(className)) {
        throw new IllegalArgumentException("Duplicate data fetch service for " + className);
      }
      logger.info("Registering {} {} callback size {}", className, service, onRegisterCallbacks.size());
      services.put(className, service);
    }
    
    // Copy matching consumers (so we can accept them when not synchronized)
    List<Consumer<S>> consumers;
    synchronized (onRegisterCallbacks) {
      consumers = new ArrayList<>(onRegisterCallbacks.size());
      int i = 0;
      while (i < onRegisterCallbacks.size()) {
        Callback<S> callback = onRegisterCallbacks.get(i);
        logger.info("Callback {}: {}", i, callback.className);
        if (callback.className.equals(className)) {
          consumers.add(callback.consumer);
          //callback.consumer.accept(service);
          onRegisterCallbacks.remove(i);
        } else {
          i++;
        }
      }
    }
    
    // Now accept all the consumers
    for (Consumer<S> consumer : consumers) {
      consumer.accept(service);
    }
  }
  
  
  @Override
  public void unregister (String className) {
    S service;
    
    synchronized (services) {
      logger.info("Unregistering {}", className);
      service = services.remove(className);
      
      if (service == null) {
        logger.error("No data fetch service for: {}", className);
        return;
      }
    }
    
    // Copy matching consumers (so we can accept them when not synchronized
    List<Consumer<S>> consumers;
    synchronized (onRegisterCallbacks) {
      consumers = new ArrayList<>(onUnregisterCallbacks.size());
      int i = 0;
      while (i < onUnregisterCallbacks.size()) {
        Callback<S> callback = onUnregisterCallbacks.get(i);
        if (callback.className.equals(className)) {
          consumers.add(callback.consumer);
          //callback.consumer.accept(service);
          onUnregisterCallbacks.remove(i);
        } else {
          i++;
        }
      }
    }
    
    // Now accept all the consumers
    for (Consumer<S> consumer : consumers) {
      consumer.accept(service);
    }
  }
  
  
  @Override
  public void getService(String className, Consumer<S> onRegister) {
    logger.info ("Get data fetch service for {}", className);
    
    S service = null;
    synchronized (services) {
      service = services.get(className);
      if (service != null) {
        logger.info ("{} found, so returing via onRegsiter consumer", service);
        onRegister.accept(service);
      }
    }
    
    if (service == null) {
      synchronized (onRegisterCallbacks) {
        logger.info ("no fetch service found, so adding to callback [{}]", onRegisterCallbacks.size());
        Callback<S> callback = new Callback<>(className, onRegister);
        onRegisterCallbacks.add(callback);
      }
    }
  }
  
    
  @Override
  public void getService(String className, Consumer<S> onRegister, Consumer<S> onUnregister) {
    getService (className, onRegister);

    synchronized (onUnregisterCallbacks) {
      Callback<S> callback2 = new Callback<>(className, onUnregister);
      onUnregisterCallbacks.add(callback2);
    }
  }

}
