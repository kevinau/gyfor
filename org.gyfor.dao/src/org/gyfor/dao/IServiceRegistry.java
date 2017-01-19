package org.gyfor.dao;

import java.util.function.Consumer;

public interface IServiceRegistry<S> {

  public void register (String className, S service);

  
  public void unregister (String classNae);


  public void getService(String className, Consumer<S> onRegister);

  
  public void getService(String className, Consumer<S> onRegister, Consumer<S> onUnregister);
  
}
