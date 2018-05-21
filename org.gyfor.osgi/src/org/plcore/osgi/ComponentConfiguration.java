package org.plcore.osgi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.osgi.service.component.ComponentContext;
import org.plcore.value.ExistingDirectory;


public class ComponentConfiguration {

  private static class PartialDictionaryMap implements Map<String, Object> {

    private final Dictionary<String, Object> dict;


    PartialDictionaryMap(Dictionary<String, Object> dict) {
      this.dict = dict;
    }


    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }


    @Override
    public boolean containsKey(Object key) {
      throw new UnsupportedOperationException();
    }


    @Override
    public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
    }


    @Override
    public Set<Entry<String, Object>> entrySet() {
      throw new UnsupportedOperationException();
    }


    @Override
    public Object get(Object key) {
      return dict.get(key);
    }


    @Override
    public boolean isEmpty() {
      return dict.isEmpty();
    }


    @Override
    public Set<String> keySet() {
      Set<String> keys = new HashSet<>();
      for (Enumeration<String> e = dict.keys(); e.hasMoreElements();) {
        String key = e.nextElement();
        keys.add(key);
      }
      return keys;
    }


    @Override
    public Object put(String key, Object value) {
      throw new UnsupportedOperationException();
    }


    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
      throw new UnsupportedOperationException();
    }


    @Override
    public Object remove(Object key) {
      throw new UnsupportedOperationException();
    }


    @Override
    public int size() {
      return dict.size();
    }


    @Override
    public Collection<Object> values() {
      throw new UnsupportedOperationException();
    }
  }


  public static void load(Object target, ComponentContext context) {
    if (context != null) {
      Map<String, Object> props = new PartialDictionaryMap(context.getProperties());
      load(target, props);
    }
  }


  public static void load(Object target, Map<String, Object> props) {
    if (props != null) {
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
            if (fieldClass.isArray()) {
              ArrayList<Object> list = new ArrayList<>();
              String prefix = propertyName + ".";

              for (String key : props.keySet()) {
                if (key.equals(propertyName) || key.startsWith(prefix)) {
                  String propertyValue = props.get(key).toString();
                  Object fieldValue = getFieldValue(field.getType(), propertyValue);
                  list.add(fieldValue);
                }
              }
              Object[] array = list.toArray();
              field.setAccessible(true);
              field.set(target, array);
            } else {
              System.out.println("==============" + field.getType() + "   " + propertyName);

              Object propertyValue = props.get(propertyName);
              if (propertyValue != null) {
                Object fieldValue = getFieldValue(field.getType(), propertyValue.toString());
                field.setAccessible(true);
                field.set(target, fieldValue);
              } else if (configAnn.required()) {
                System.out.println("Dictionary elements: " + props.size());
                for (String key : props.keySet()) {
                  Object value = props.get(key);
                  System.out.println("Dictionary: " + key + " = " + value);
                }
                throw new IllegalConfigurationException(
                    "Configuration value '" + propertyName + "' required for " + klass.getSimpleName());
              }
            }
          }
        }
      } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException
          | InvocationTargetException | ClassNotFoundException ex) {
        throw new RuntimeException(ex);
      }
    }
  }


  private static Object getFieldValue(Class<?> type, String propertyValue) throws InstantiationException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
    Object value;

    if (type.isEnum()) {
      value = getPropertyAsEnum(propertyValue, type);
    } else if (ExistingDirectory.class.isAssignableFrom(type)) {
      value = new ExistingDirectory(Paths.get(propertyValue));
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
        throw new RuntimeException("Expecting true or false for type: " + type.getCanonicalName());
      }
    } else if (Class.class.isAssignableFrom(type)) {
      value = Class.forName(propertyValue);
    } else {
      try {
        Constructor<?> constructor = type.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        value = constructor.newInstance(propertyValue);
      } catch (NoSuchMethodException ex) {
        if (String.class.isAssignableFrom(type)) {
          value = (String)propertyValue;
        } else {
          throw new RuntimeException("Unsupported data type: " + type.getCanonicalName());
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
