package org.gyfor.dao.registry;

import java.util.function.BiConsumer;


public interface IBiServiceRegsitry<S, T> {

  void register(String name, S service);


  void unregister(String name, S dummy);


  void getServices(String name1, String name2, BiConsumer<S, T> onRegister);


  void getService(String name1, String name2, BiConsumer<S, T> onRegister, BiConsumer<S, T> onUnregister);

}