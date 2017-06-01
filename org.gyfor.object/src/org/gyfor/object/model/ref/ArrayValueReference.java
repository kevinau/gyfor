package org.gyfor.object.model.ref;

public abstract class ArrayValueReference implements IValueReference {

  private final int index;
  
  
  public ArrayValueReference (int index) {
    this.index = index;
  }
  
  
  @Override
  public String toString() {
    return "ArrayContainerReference [" + index + "]";
  }

  
  protected abstract Object[] getContainer();

  
  @Override
  public <T> void setValue(T value) {
    Object[] container = getContainer();
    container[index] = value;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    Object[] container = getContainer();
    return (T)container[index];
  }

  
}
