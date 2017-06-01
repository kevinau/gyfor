package org.gyfor.object.model.ref;

import java.lang.reflect.Field;

public abstract class ClassValueReference implements IValueReference {

  private final Field field;
  
  
  public ClassValueReference (Field field) {
    if (field == null) {
      throw new IllegalArgumentException("'field' argument must not be null");
    }
    this.field = field;
  }
  
    
  @Override
  public String toString() {
    return "ClassValueReference [" + field + "]";
  }

  
  protected abstract Object getInstance();

  
  @Override
  public <T> void setValue(T value) {
    try {
      field.setAccessible(true);
      field.set(getInstance(), value);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    T value;
    try {
      System.out.println("1........" + field.getName());
      Object inst = getInstance();
      System.out.println("2........" + inst + "  " + inst.getClass() + "  " + field);
      System.out.println("3........." + field.getType());
      field.setAccessible(true);
      Object x = field.get(inst);
      System.out.println("4........" + x);
      if (x != null) {
        System.out.println("5........." + x.getClass());
      }
      
      value = (T)field.get(getInstance());
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    return value;
  }

}
