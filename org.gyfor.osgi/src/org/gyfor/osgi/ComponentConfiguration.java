package org.gyfor.osgi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;


public class ComponentConfiguration {

  public static void load(Object target, ComponentContext context) {
    if (context != null) {
      Dictionary<String, Object> dict = context.getProperties();

//      System.out.println();
//      for (Enumeration<String> e = dict.keys(); e.hasMoreElements();) {
//        String n = e.nextElement();
//        Object v = dict.get(n);
//        System.out.println("############# " + n + "=" + v);
//      }

      try {
        Class<?> klass = target.getClass();
        Field[] fields = klass.getDeclaredFields();
        for (Field field : fields) {
          Configurable configAnn = field.getAnnotation(Configurable.class);
          if (configAnn != null) {
            String propertyName = configAnn.name();
            if (propertyName.length() == 0) {
              propertyName = field.getName();
            }
            Class<?> fieldClass = field.getType();
            if (List.class.isAssignableFrom(fieldClass)) {
              // The list is assumed to be a list of String
              ArrayList<String> list = new ArrayList<>();
              String prefix = propertyName + ".";
              
              for (Enumeration<String> e = dict.keys(); e.hasMoreElements(); ) {
                String key = e.nextElement();
                if (key.equals(propertyName) || key.startsWith(prefix)) {
                  String value = dict.get(key).toString();
                  list.add(value);
                }
              }
              field.setAccessible(true);
              field.set(target, list);
            } else {
              String propertyValue = (String)dict.get(propertyName);
              if (propertyValue != null) {
                Object fieldValue = getFieldValue(field.getType(), propertyValue);
                field.setAccessible(true);
                field.set(target, fieldValue);
              } else if (configAnn.required()) {
                throw new IllegalConfigurationException(
                    "Configuration value '" + propertyName + "' required for " + klass.getSimpleName());
              }
            }
          }
        }
      } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException
          | InvocationTargetException ex) {
        throw new RuntimeException(ex);
      }
    }
  }


  private static Object getFieldValue(Class<?> type, String propertyValue) throws InstantiationException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Object value;
    
    if (type.isEnum()) {
      value = getPropertyAsEnum(propertyValue, type);
    } else if (Path.class.isAssignableFrom(type)) {
      value = Paths.get(propertyValue);
    } else if (Pattern.class.isAssignableFrom(type)) {
      value = Pattern.compile(propertyValue);
    } else if (Boolean.TYPE.isAssignableFrom(type)) {
      switch (propertyValue.toLowerCase()) {
      case "true" :
        value = true;
        break;
      case "false" :
        value = false;
        break;
      default :
        throw new RuntimeException ("Expecting true or false for type: " + type.getCanonicalName());
      }
    } else {
      try {
        Constructor<?> constructor = type.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        value = constructor.newInstance(propertyValue);
      } catch (NoSuchMethodException ex) {
        if (String.class.isAssignableFrom(type)) {
          value = (String)propertyValue;
        } else {
          throw new RuntimeException ("Unsupported data type: " + type.getCanonicalName());
        }
      }
    }
    return value;
  }


  private static <E> E getPropertyAsEnum(String property, Class<E> enumClass) {
    E[] values = enumClass.getEnumConstants();
    for (E value : values) {
      if (value.toString().equals(property)) {
        return value;
      }
    }
    try {
      int i = Integer.parseInt(property);
      if (i >= 0 && i < values.length) {
        return values[i];
      } else {
        throw new IllegalArgumentException(property);
      }
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(property);
    }
  }

}
