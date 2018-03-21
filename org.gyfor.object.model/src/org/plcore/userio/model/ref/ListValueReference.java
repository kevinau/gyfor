package org.plcore.userio.model.ref;

import java.util.List;

import org.plcore.userio.model.ref.IValueReference;

public class ListValueReference implements IValueReference {

  private final IValueReference parentRef;
  private final int index;
  
  
  public ListValueReference (IValueReference parentRef, int index) {
    this.parentRef = parentRef;
    this.index = index;
  }
  
  
  @Override
  public String toString() {
    return "ListValueReference [" + index + "]";
  }

  
  @Override
  public <T> void setValue(T value) {
    List<Object> container = parentRef.getValue();
    container.set(index, value);
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    List<Object> container = parentRef.getValue();
    return (T)container.get(index);
  }

  
  @Override
  public String getName() {
    return Integer.toString(index);
  }
  
}
