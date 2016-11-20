package org.gyfor.object.model.ref;

public class EntityValueReference implements IValueReference {

  private Object instance;
  
  @Override
  public String toString() {
    return "EntityValueReference []";
  }

  
  @Override
  public <T> void setValue(T instance) {
    this.instance = instance;
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    return (T)instance;
  }

}
