package org.gyfor.util;


public interface ListChangeListener<E> {

  public void elementAdded(E elem);
  
  public void elementChanged(E elem);
  
  public void elementRemoved(E elem);
  
}
