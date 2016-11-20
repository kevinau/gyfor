package org.gyfor.object.model.ref;

public interface IValueReference {

  public <T> void setValue (T value);
  
  public <T> T getValue ();
  
}
