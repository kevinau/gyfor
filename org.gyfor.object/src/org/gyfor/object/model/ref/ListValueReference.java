package org.gyfor.object.model.ref;

import java.util.List;

public abstract class ListValueReference implements IValueReference {

  private final int index;
  
  
  public ListValueReference (int index) {
    if (index < 0 || index == Integer.MAX_VALUE) {
      throw new IllegalArgumentException("'index' argument must not be negative or MAX_VALUE");
    }
    this.index = index;
  }
  
  
  
  @Override
  public String toString() {
    return "ListValueReference [" + index + "]";
  }

  
  protected abstract List<Object> getContainer();
  

  @Override
  public <T> void setValue(T value) {
    List<Object> container = getContainer();
    container.set(index, value);
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    List<Object> container = getContainer();
    return (T)container.get(index);
  }

}
