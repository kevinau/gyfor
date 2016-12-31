package org.gyfor.object.model.ref;

public class ArrayValueReference implements IValueReference {

  private final Object[] container;
  private final int index;
  
  
  public ArrayValueReference (Object[] container, int index) {
    this.container = container;
    this.index = index;
  }
  
  
  @Override
  public String toString() {
    return "ArrayContainerReference [" + container + ", " + index + "]";
  }


  @Override
  public <T> void setValue(T value) {
    container[index] = value;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    return (T)container[index];
  }

  
}
