package org.gyfor.util;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class SingletonIterator<X> implements Iterator<X> {

  private boolean gotItem = false;
  private final X item;
  
  
  public SingletonIterator (X item) {
    this.item = item;
  }
  
  
  @Override
  public boolean hasNext() {
    return !this.gotItem;
  }

  @Override
  public X next() {
    if (this.gotItem) {
      throw new NoSuchElementException();
    }
    this.gotItem = true;
    return item;
  }

  @Override
  public void remove() {
    if (!this.gotItem) {
      this.gotItem = true;
    } else {
      throw new NoSuchElementException();
    }
  }

}
