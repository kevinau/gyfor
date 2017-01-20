package org.gyfor.dao.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.gyfor.dao.IServiceRegistry;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class BiServiceRegistry<S, T> implements IBiServiceRegsitry<S, T> {

  private final Logger logger = LoggerFactory.getLogger(BiServiceRegistry.class);
  
  
  private final Map<String, S> services1 = new HashMap<>();
  private final Map<String, T> services2 = new HashMap<>();
  private final Object LOCK = new Object();


  private static class Callback<S,T> {
    private String name1;
    private S service1;
    private String name2;
    private T service2;
    private BiConsumer<S,T> consumer;
    
 
    private Callback (String name1, S service1, String name2, T service2, BiConsumer<S,T> consumer) {
      this.name1 = name1;
      this.service1 = service1;
      this.name2 = name2;
      this.service2 = service2;
      this.consumer = consumer;
    }
  }
  

  private List<Callback<S,T>> onRegisterCallbacks = new ArrayList<>();
  private List<Callback<S,T>> onUnregisterCallbacks = new ArrayList<>();
  
  
  /* (non-Javadoc)
   * @see org.gyfor.dao.registry.IBiServiceRegsitry#register(java.lang.String, S)
   */
  @Override
  public void register (String name, S service) {
    synchronized (services1) {
      if (services1.containsKey(name)) {
        throw new IllegalArgumentException("Duplicate service for " + name);
      }
      logger.info("Registering {} {} callback size {}", name, service, onRegisterCallbacks.size());
      services1.put(name, service);
    }
    
    // Copy matching consumers (so we can accept them when not synchronized)
    List<Callback<S,T>> callbacks;
    synchronized (onRegisterCallbacks) {
      callbacks = new ArrayList<>(onRegisterCallbacks.size());
      int i = 0;
      while (i < onRegisterCallbacks.size()) {
        Callback<S,T> callback = onRegisterCallbacks.get(i);
        logger.info("Callback {}: {} {}", i, callback.name1, callback.name2);
        if (callback.name1.equals(name) && callback.service2 != null) {
          // Name matches, and both services are now present
          callback.service1 = service;
          callbacks.add(callback);
          onRegisterCallbacks.remove(i);
        } else {
          i++;
        }
      }
    }
    
    // Now accept all the consumers
    for (Callback<S,T> callback : callbacks) {
      callback.consumer.accept(callback.service1, callback.service2);
    }
  }
  
  
  /* (non-Javadoc)
   * @see org.gyfor.dao.registry.IBiServiceRegsitry#unregister(java.lang.String, S)
   */
  @Override
  public void unregister (String name, S dummy) {
    S service;
    synchronized (services1) {
      logger.info("Unregistering {}", name);
      service = services1.remove(name);
      
      if (service == null) {
        logger.error("No service for: {}", name);
        return;
      }
    }
    
    // Copy matching consumers (so we can accept them when not synchronized)
    List<Callback<S,T>> callbacks;
    synchronized (onRegisterCallbacks) {
      callbacks = new ArrayList<>(onUnregisterCallbacks.size());
      int i = 0;
      while (i < onUnregisterCallbacks.size()) {
        Callback<S,T> callback = onUnregisterCallbacks.get(i);
        if (callback.name1.equals(name)&& callback.service2 != null) {
          // Name matches.  Both services have now been unregistered
          callback.service1 = service;
          callbacks.add(callback);
          onUnregisterCallbacks.remove(i);
        } else {
          i++;
        }
      }
    }
    
    // Now accept all the consumers
    for (Callback<S,T> callback : callbacks) {
      callback.consumer.accept(callback.service1,callback.service2);
    }
  }
  
  
  /* (non-Javadoc)
   * @see org.gyfor.dao.registry.IBiServiceRegsitry#getServices(java.lang.String, java.lang.String, java.util.function.BiConsumer)
   */
  @Override
  public void getServices(String name1, String name2, BiConsumer<S,T> onRegister) {
    logger.info ("Get service for {} and {}", name1, name2);
    
    S service1 = null;
    T service2 = null;
    synchronized (LOCK) {
      service1 = services1.get(name1);
      service2 = services2.get(name2);
      if (service1 != null && service2 != null) {
        logger.info ("Services {} and {} found, so returing via onRegsiter consumer", service1,service2);
        onRegister.accept(service1, service2);
        return;
      }
    }
    
    if (serviceSet == null) {
      synchronized (onRegisterCallbacks) {
        logger.info ("no fetch service found, so adding to callback [{}]", onRegisterCallbacks.size());
        Callback<S, T> callback = new Callback<>(name1, service1, name2, service2, onRegister);
        onRegisterCallbacks.add(callback);
      }
    }
  }
  
    
  /* (non-Javadoc)
   * @see org.gyfor.dao.registry.IBiServiceRegsitry#getService(java.lang.String, java.lang.String, java.util.function.BiConsumer, java.util.function.BiConsumer)
   */
  @Override
  public void getService(String name1, String name2, BiConsumer<S,T> onRegister, BiConsumer<S,T> onUnregister) {
    getService (name, onRegister);

    synchronized (onUnregisterCallbacks) {
      Callback<S,T> callback2 = new Callback<>(name1, null, name2, null, onUnregister);
      onUnregisterCallbacks.add(callback2);
    }
  }

}
