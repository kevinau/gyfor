package org.gyfor.object.plan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public interface ILabelGroup {

  public default String get(String name) {
    String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);  
    String label;
    try {
      Method getMethod = getClass().getMethod(methodName);
      label = (String)getMethod.invoke(this);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
    return label;
  }

  
  public default void loadNotEmpty (Map<String, Object> context, String[] names, String... values) {
    for (int i = 0; i < names.length; i++) {
      String value = values[i];
      if (value == null || value.length() == 0) {
        // do not load
      } else {
        context.put(names[i], value);
      }
    }
  }
  
  
  public void loadAll (Map<String, Object> context);
  
}
