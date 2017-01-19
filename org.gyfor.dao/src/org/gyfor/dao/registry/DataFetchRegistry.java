package org.gyfor.dao.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.gyfor.dao.IDataFetchRegistry;
import org.gyfor.dao.IDataFetchService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class DataFetchRegistry extends ServiceRegistry<IDataFetchService> implements IDataFetchRegistry {

//  private final Logger logger = LoggerFactory.getLogger(DataFetchRegistry.class);
//  
//  
//  private Map<String, IDataFetchService> fetchServices = new HashMap<>();
//  
//  private static class Callback {
//    private String className;
//    private Consumer<IDataFetchService> consumer;
// 
//    private Callback (String className, Consumer<IDataFetchService> consumer) {
//      this.className = className;
//      this.consumer = consumer;
//    }
//  }
//  
//  private List<Callback> onRegisterCallbacks = new ArrayList<>();
//  private List<Callback> onUnregisterCallbacks = new ArrayList<>();
//  
//  
//  @Override
//  public void register (String className, IDataFetchService fetchService) {
//    synchronized (fetchServices) {
//      if (fetchServices.containsKey(className)) {
//        throw new IllegalArgumentException("Duplicate data fetch service for " + className);
//      }
//      logger.info("Registering {} {} callback size {}", className, fetchService, onRegisterCallbacks.size());
//      fetchServices.put(className, fetchService);
//    }
//    
//    // Copy matching consumers (so we can accept them when not synchronized
//    List<Consumer<IDataFetchService>> consumers;
//    synchronized (onRegisterCallbacks) {
//      consumers = new ArrayList<>(onRegisterCallbacks.size());
//      int i = 0;
//      while (i < onRegisterCallbacks.size()) {
//        Callback callback = onRegisterCallbacks.get(i);
//        logger.info("Callback {}: {}", i, callback.className);
//        if (callback.className.equals(className)) {
//          consumers.add(callback.consumer);
//          //callback.consumer.accept(fetchService);
//          onRegisterCallbacks.remove(i);
//        } else {
//          i++;
//        }
//      }
//    }
//    
//    // Now accept all the consumers
//    for (Consumer<IDataFetchService> consumer : consumers) {
//      consumer.accept(fetchService);
//    }
//  }
//  
//  
//  @Override
//  public void unregister (String className) {
//    IDataFetchService fetchService;
//    
//    synchronized (fetchServices) {
//      logger.info("Unregistering {}", className);
//      fetchService = fetchServices.remove(className);
//      
//      if (fetchService == null) {
//        logger.error("No data fetch service for: {}", className);
//        return;
//      }
//    }
//    
//    // Copy matching consumers (so we can accept them when not synchronized
//    List<Consumer<IDataFetchService>> consumers;
//    synchronized (onRegisterCallbacks) {
//      consumers = new ArrayList<>(onUnregisterCallbacks.size());
//      int i = 0;
//      while (i < onUnregisterCallbacks.size()) {
//        Callback callback = onUnregisterCallbacks.get(i);
//        if (callback.className.equals(className)) {
//          consumers.add(callback.consumer);
//          //callback.consumer.accept(fetchService);
//          onUnregisterCallbacks.remove(i);
//        } else {
//          i++;
//        }
//      }
//    }
//    
//    // Now accept all the consumers
//    for (Consumer<IDataFetchService> consumer : consumers) {
//      consumer.accept(fetchService);
//    }
//  }
//  
//  
//  @Override
//  public void getFetchService(String className, Consumer<IDataFetchService> onRegister) {
//    logger.info ("Get data fetch service for {}", className);
//    
//    IDataFetchService fetchService = null;
//    synchronized (fetchServices) {
//      fetchService = fetchServices.get(className);
//      if (fetchService != null) {
//        logger.info ("{} found, so returing via onRegsiter consumer", fetchService);
//        onRegister.accept(fetchService);
//      }
//    }
//    
//    if (fetchService == null) {
//      synchronized (onRegisterCallbacks) {
//        logger.info ("no fetch service found, so adding to callback [{}]", onRegisterCallbacks.size());
//        Callback callback = new Callback(className, onRegister);
//        onRegisterCallbacks.add(callback);
//      }
//    }
//  }
//  
//    
//  @Override
//  public void getFetchService(String className, Consumer<IDataFetchService> onRegister, Consumer<IDataFetchService> onUnregister) {
//    getFetchService (className, onRegister);
//
//    synchronized (onUnregisterCallbacks) {
//      Callback callback2 = new Callback(className, onUnregister);
//      onUnregisterCallbacks.add(callback2);
//    }
//  }

}
