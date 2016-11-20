package org.gyfor.util;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;


public class ArrayIterator<T> implements java.util.Iterator<T> {

  /** The array to iterate over */
  protected Object array;
  /** The end index to loop to */
  protected int endIndex = 0;
  /** The current iterator index */
  protected int index = 0;


  /**
   * Constructs an ArrayIterator that will iterate over the values in the
   * specified array.
   *
   * @param array
   *          the array to iterate over.
   * @throws IllegalArgumentException
   *           if <code>array</code> is not an array.
   * @throws NullPointerException
   *           if <code>array</code> is <code>null</code>
   */
  public ArrayIterator(final Object array) {
    this.endIndex = Array.getLength(array);
    this.array = array;
    this.index = 0;
  }


  /**
   * Returns true if there are more elements to return from the array.
   *
   * @return true if there is a next element to return
   */
  @Override
  public boolean hasNext() {
    return (index < endIndex);
  }


  /**
   * Returns the next element in the array.
   *
   * @return the next element in the array
   * @throws NoSuchElementException
   *           if all the elements in the array have already been returned
   */
  @SuppressWarnings("unchecked")
  @Override
  public T next() {
    if (hasNext() == false) {
      throw new NoSuchElementException();
    }
    return (T)Array.get(array, index++);
  }


  /**
   * Throws {@link UnsupportedOperationException}.
   *
   * @throws UnsupportedOperationException
   *           always
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("remove() method is not supported");
  }

}
