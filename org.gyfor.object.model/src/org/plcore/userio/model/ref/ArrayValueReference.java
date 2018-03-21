package org.plcore.userio.model.ref;

import org.plcore.userio.model.ref.IValueReference;

public class ArrayValueReference implements IValueReference {

  private final IValueReference parentRef;
  private final int index;
  
  
  public ArrayValueReference (IValueReference parentRef, int index) {
    this.parentRef = parentRef;
    this.index = index;
  }
  
  
  @Override
  public String toString() {
    return "ArrayValueReference [" + index + "]";
  }

  
  @Override
  public <T> void setValue(T value) {
    Object[] container = parentRef.getValue();
    container[index] = value;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    Object[] container = parentRef.getValue();
    return (T)container[index];
  }

  
  @Override
  public String getName() {
    return Integer.toString(index);
  }
  
}
