package org.plcore.userio.model.ref;

public interface IValueReference {

  public <T> void setValue (T value);
  
  public <T> T getValue ();
  
  public String getName();
  
}
