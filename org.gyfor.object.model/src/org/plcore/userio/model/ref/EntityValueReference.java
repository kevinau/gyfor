package org.plcore.userio.model.ref;

import org.plcore.userio.model.ref.IValueReference;

public class EntityValueReference implements IValueReference {

  private Object value;
  
  @Override
  public String toString() {
    return "EntityValueReference []";
  }

  
  @Override
  public <T> void setValue(T value) {
    this.value = value;
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getValue() {
    return (T)value;
  }


  @Override
  public String getName() {
    return value.getClass().getName();
  }
 
}
