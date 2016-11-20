package org.gyfor.object.model.ref;

import java.util.List;

public class ListValueReference implements IValueReference {

  private final List<Object> instance;
  private final int index;
  
  
  public ListValueReference (List<Object> instance, int index) {
    if (instance == null) {
      throw new IllegalArgumentException("'instance' argument must not be null");
    }
    if (instance instanceof IValueReference) {
      throw new IllegalArgumentException(instance.toString());
    }
    if (index < 0 || index == Integer.MAX_VALUE) {
      throw new IllegalArgumentException("'index' argument must not be negative or MAX_VALUE");
    }
    this.instance = instance;
    this.index = index;
  }
  
  
  
  @Override
  public String toString() {
    return "ListValueReference [" + instance + ", " + index + "]";
  }

  
  @Override
  public <T> void setValue(T value) {
    instance.set(index, value);
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    return (T)instance.get(index);
  }

}
