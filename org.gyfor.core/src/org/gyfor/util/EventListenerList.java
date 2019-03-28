/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.util;

import java.util.EventListener;
import java.util.Iterator;


public class EventListenerList<E extends EventListener> implements Iterable<E> {

  /* A null array to be shared by all empty listener lists */
  @SuppressWarnings("unchecked")
  private final E[] NULL_ARRAY = (E[]) new EventListener[0];

  /* The list of listeners */
  private transient E[] listenerList = NULL_ARRAY;

  /**
   * Adds the listener as a listener of the specified type.
   * 
   * @param x the listener to be added
   */
  @SuppressWarnings("unchecked")
  public synchronized void add(EventListener x) {
    if (x == null) {
      // In an ideal world, we would do an assertion here
      // to help developers know they are probably doing
      // something wrong
      return;
    }
    if (listenerList == NULL_ARRAY) {
      // if this is the first listener added,
      // initialize the lists
      listenerList = (E[]) new EventListener[] { x };
    } else {
      // Otherwise copy the array and add the new listener
      int i = listenerList.length;
      EventListener[] tmp = new EventListener[i + 1];
      System.arraycopy(listenerList, 0, tmp, 0, i);
      tmp[i] = x;
      listenerList = (E[]) tmp;
    }
  }

  /**
   * Removes the listener as a listener of the specified type.
   * 
   * @param x the listener to be removed
   */
  @SuppressWarnings("unchecked")
  public synchronized void remove(EventListener x) {
    if (x == null) {
      // In an ideal world, we would do an assertion here
      // to help developers know they are probably doing
      // something wrong
      return;
    }
    // Is x on the list?
    int index = -1;
    for (int i = listenerList.length - 1; i >= 0; i--) {
      if (listenerList[i].equals(x) == true) {
        index = i;
        break;
      }
    }

    // If so, remove it
    if (index != -1) {
      EventListener[] tmp = new EventListener[listenerList.length - 1];
      // Copy the list up to index
      if (index > 0) {
        System.arraycopy(listenerList, 0, tmp, 0, index);
      }
      // Copy from two past the index, up to
      // the end of tmp (which is one element
      // shorter than the old list)
      if (index < tmp.length) {
        System.arraycopy(listenerList, index + 1, tmp, index, tmp.length - index);
      }
      // set the listener array to the new array or null
      listenerList = (tmp.length == 0) ? NULL_ARRAY : (E[]) tmp;
    }
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      private int pos = 0;

      @Override
      public boolean hasNext() {
        return listenerList.length > pos;
      }

      @Override
      public E next() {
        return listenerList[pos++];
      }

    };
  }
}
