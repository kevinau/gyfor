package org.plcore.osgi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;


/**
 * A simplified, thread-safe variant of {@link java.util.ArrayList}, suitable
 * for use in OSGi components. This list can hold component references, when
 * the @Referecne multiple (0..n or 1..n) and dynamic.
 * 
 * <p>
 * Only those methods rquired by OSGi are implemented. In particular, no bulk
 * operations are supported. All unsupported methods throw an
 * UnsupportedOperationException. Element comparison uses identity "==", not
 * "equals". {@code null} elements are not permitted.
 * 
 * <p>
 * This class does not implement Serializable, and the equals and hashCode
 * methods are not supported.
 * 
 * <p>
 * This list implements the "copy on write" semantics that the
 * {@link java.util.concurrent.CopyOnWriteArrayList} uses.
 */
public class MultipleReferenceList<E> implements List<E> {

  /** The lock protecting all mutators */
  private final transient ReentrantLock lock = new ReentrantLock();

  /** The underlying array. */
  private transient volatile Object[] array;


  /**
   * static version of indexOf, to allow repeated calls without needing to
   * re-acquire array each time.
   * 
   * @param o
   *          element to search for
   * @param elements
   *          the array
   * @param index
   *          first index to search
   * @param fence
   *          one past last index to search
   * @return index of element, or -1 if absent
   */
  private static int indexOf(Object o, Object[] elements, int index, int fence) {
    for (int i = index; i < fence; i++) {
      if (o == elements[i]) {
        return i;
      }
    }
    return -1;
  }


  /**
   * static version of lastIndexOf.
   * 
   * @param o
   *          element to search for
   * @param elements
   *          the array
   * @param index
   *          first index to search
   * @return index of element, or -1 if absent
   */
  private static int lastIndexOf(Object o, Object[] elements, int index) {
    for (int i = index; i >= 0; i--) {
      if (o == elements[i]) {
        return i;
      }
    }
    return -1;
  }


  /**
   * Creates an empty list.
   */
  public MultipleReferenceList() {
    array = new Object[0];
  }


  /**
   * Appends the specified element to the end of this list.
   *
   * @param e
   *          element to be appended to this list
   * @return {@code true} (as specified by {@link Collection#add})
   */
  @Override
  public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Object[] elements = array;
      int len = elements.length;
      Object[] newArray = Arrays.copyOf(elements, len + 1);
      newArray[len] = e;
      array = newArray;
      return true;
    } finally {
      lock.unlock();
    }
  }


  /**
   * Inserts the specified element at the specified position in this list.
   * Shifts the element currently at that position (if any) and any subsequent
   * elements to the right (adds one to their indices).
   *
   * @throws IndexOutOfBoundsException
   *           {@inheritDoc}
   */
  @Override
  public void add(int index, E element) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Object[] elements = array;
      int len = elements.length;
      if (index > len || index < 0)
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
      Object[] newArray;
      int numMoved = len - index;
      if (numMoved == 0)
        newArray = Arrays.copyOf(elements, len + 1);
      else {
        newArray = new Object[len + 1];
        System.arraycopy(elements, 0, newArray, 0, index);
        System.arraycopy(elements, index, newArray, index + 1, numMoved);
      }
      newArray[index] = element;
      array = newArray;
    } finally {
      lock.unlock();
    }
  }


  @Override
  public boolean addAll(Collection<? extends E> arg0) {
    throw new UnsupportedOperationException();
  }


  @Override
  public boolean addAll(int arg0, Collection<? extends E> arg1) {
    throw new UnsupportedOperationException();
  }


  /**
   * Appends the element, if not present.
   *
   * @param e
   *          element to be added to this list, if absent
   * @return {@code true} if the element was added
   */
  public boolean addIfAbsent(E e) {
    Object[] snapshot = array;
    return indexOf(e, snapshot, 0, snapshot.length) >= 0 ? false : addIfAbsent(e, snapshot);
  }


  /**
   * A version of addIfAbsent using the strong hint that given recent snapshot
   * does not contain e.
   */
  private boolean addIfAbsent(E e, Object[] snapshot) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Object[] current = array;
      int len = current.length;
      if (snapshot != current) {
        // Optimize for lost race to another addXXX operation
        int common = Math.min(snapshot.length, len);
        for (int i = 0; i < common; i++) {
          if (current[i] != snapshot[i] && e == current[i]) {
            return false;
          }
        }
        if (indexOf(e, current, common, len) >= 0) {
          return false;
        }
      }
      Object[] newArray = Arrays.copyOf(current, len + 1);
      newArray[len] = e;
      array = newArray;
      return true;
    } finally {
      lock.unlock();
    }
  }


  /**
   * Removes all of the elements from this list. The list will be empty after
   * this call returns.
   */
  @Override
  public void clear() {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      array = new Object[0];
    } finally {
      lock.unlock();
    }
  }


  /**
   * Returns {@code true} if this list contains the specified element. More
   * formally, returns {@code true} if and only if this list contains at least
   * one element {@code e} such that
   * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
   *
   * @param o
   *          element whose presence in this list is to be tested
   * @return {@code true} if this list contains the specified element
   */
  @Override
  public boolean contains(Object o) {
    Object[] elements = array;
    return indexOf(o, elements, 0, elements.length) >= 0;
  }


  @Override
  public boolean containsAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }


  @Override
  public void forEach(Consumer<? super E> action) {
    if (action == null)
      throw new NullPointerException();
    Object[] elements = array;
    int len = elements.length;
    for (int i = 0; i < len; ++i) {
      @SuppressWarnings("unchecked")
      E e = (E)elements[i];
      action.accept(e);
    }
  }


  @Override
  public E get(int index) {
    return get(array, index);
  }


  @SuppressWarnings("unchecked")
  private E get(Object[] a, int index) {
    return (E)a[index];
  }


  /**
   * Returns the index of the first occurrence of the specified element in this
   * list, searching forwards from {@code index}, or returns -1 if the element
   * is not found. More formally, returns the lowest index {@code i} such that
   * <tt>(i&nbsp;&gt;=&nbsp;index&nbsp;&amp;&amp;&nbsp;(e==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;e.equals(get(i))))</tt>,
   * or -1 if there is no such index.
   *
   * @param e
   *          element to search for
   * @param index
   *          index to start searching from
   * @return the index of the first occurrence of the element in this list at
   *         position {@code index} or later in the list; {@code -1} if the
   *         element is not found.
   * @throws IndexOutOfBoundsException
   *           if the specified index is negative
   */
  public int indexOf(E e, int index) {
    Object[] elements = array;
    return indexOf(e, elements, index, elements.length);
  }


  @Override
  public int indexOf(Object o) {
    Object[] elements = array;
    return indexOf(o, elements, 0, elements.length);
  }


  /**
   * Returns {@code true} if this list contains no elements.
   *
   * @return {@code true} if this list contains no elements
   */
  @Override
  public boolean isEmpty() {
    return size() == 0;
  }


  /**
   * Returns an iterator over the elements in this list in proper sequence.
   *
   * <p>
   * The returned iterator provides a snapshot of the state of the list when the
   * iterator was constructed. No synchronization is needed while traversing the
   * iterator. The iterator does <em>NOT</em> support the {@code remove} method.
   *
   * @return an iterator over the elements in this list in proper sequence
   */
  @Override
  public Iterator<E> iterator() {
    return new COWIterator<E>(array, 0);
  }


  /**
   * Returns the index of the last occurrence of the specified element in this
   * list, searching backwards from {@code index}, or returns -1 if the element
   * is not found. More formally, returns the highest index {@code i} such that
   * <tt>(i&nbsp;&lt;=&nbsp;index&nbsp;&amp;&amp;&nbsp;(e==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;e.equals(get(i))))</tt>,
   * or -1 if there is no such index.
   *
   * @param e
   *          element to search for
   * @param index
   *          index to start searching backwards from
   * @return the index of the last occurrence of the element at position less
   *         than or equal to {@code index} in this list; -1 if the element is
   *         not found.
   * @throws IndexOutOfBoundsException
   *           if the specified index is greater than or equal to the current
   *           size of this list
   */
  public int lastIndexOf(E e, int index) {
    Object[] elements = array;
    return lastIndexOf(e, elements, index);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int lastIndexOf(Object o) {
    Object[] elements = array;
    return lastIndexOf(o, elements, elements.length - 1);
  }


  /**
   * {@inheritDoc}
   *
   * <p>
   * The returned iterator provides a snapshot of the state of the list when the
   * iterator was constructed. No synchronization is needed while traversing the
   * iterator. The iterator does <em>NOT</em> support the {@code remove},
   * {@code set} or {@code add} methods.
   */
  @Override
  public ListIterator<E> listIterator() {
    return new COWIterator<E>(array, 0);
  }


  /**
   * {@inheritDoc}
   *
   * <p>
   * The returned iterator provides a snapshot of the state of the list when the
   * iterator was constructed. No synchronization is needed while traversing the
   * iterator. The iterator does <em>NOT</em> support the {@code remove},
   * {@code set} or {@code add} methods.
   *
   * @throws IndexOutOfBoundsException
   *           {@inheritDoc}
   */
  @Override
  public ListIterator<E> listIterator(int index) {
    Object[] elements = array;
    int len = elements.length;
    if (index < 0 || index > len)
      throw new IndexOutOfBoundsException("Index: " + index);

    return new COWIterator<E>(elements, index);
  }


  /**
   * Removes the element at the specified position in this list. Shifts any
   * subsequent elements to the left (subtracts one from their indices). Returns
   * the element that was removed from the list.
   *
   * @throws IndexOutOfBoundsException
   *           {@inheritDoc}
   */
  @Override
  public E remove(int index) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Object[] elements = array;
      int len = elements.length;
      @SuppressWarnings("unchecked")
      E oldValue = (E)elements[index];
      int numMoved = len - index - 1;
      if (numMoved == 0)
        array = Arrays.copyOf(elements, len - 1);
      else {
        Object[] newArray = new Object[len - 1];
        System.arraycopy(elements, 0, newArray, 0, index);
        System.arraycopy(elements, index + 1, newArray, index, numMoved);
        array = newArray;
      }
      return oldValue;
    } finally {
      lock.unlock();
    }
  }


  /**
   * Removes the first occurrence of the specified element from this list, if it
   * is present. If this list does not contain the element, it is unchanged.
   * More formally, removes the element with the lowest index {@code i} such
   * that
   * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
   * (if such an element exists). Returns {@code true} if this list contained
   * the specified element (or equivalently, if this list changed as a result of
   * the call).
   *
   * @param o
   *          element to be removed from this list, if present
   * @return {@code true} if this list contained the specified element
   */
  @Override
  public boolean remove(Object o) {
    Object[] snapshot = array;
    int index = indexOf(o, snapshot, 0, snapshot.length);
    return (index < 0) ? false : remove(o, snapshot, index);
  }


  /**
   * A version of remove(Object) using the strong hint that given recent
   * snapshot contains o at the given index.
   */
  private boolean remove(Object o, Object[] snapshot, int index) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Object[] current = array;
      int len = current.length;
      if (snapshot != current) {
        findIndex: {
          int prefix = Math.min(index, len);
          for (int i = 0; i < prefix; i++) {
            if (current[i] != snapshot[i] && o == current[i]) {
              index = i;
              break findIndex;
            }
          }
          if (index >= len) {
            return false;
          }
          if (current[index] == o) {
            break findIndex;
          }
          index = indexOf(o, current, index, len);
          if (index < 0) {
            return false;
          }
        }
      }
      Object[] newArray = new Object[len - 1];
      System.arraycopy(current, 0, newArray, 0, index);
      System.arraycopy(current, index + 1, newArray, index, len - index - 1);
      array = newArray;
      return true;
    } finally {
      lock.unlock();
    }
  }


  @Override
  public boolean removeAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }


  @Override
  public boolean retainAll(Collection<?> arg0) {
    throw new UnsupportedOperationException();
  }


  @Override
  public E set(int index, E element) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Object[] elements = array;
      @SuppressWarnings("unchecked")
      E oldValue = (E)elements[index];

      if (oldValue != element) {
        int len = elements.length;
        Object[] newArray = Arrays.copyOf(elements, len);
        newArray[index] = element;
        array = newArray;
      } else {
        // Not quite a no-op; ensures volatile write semantics
        array = elements;
      }
      return oldValue;
    } finally {
      lock.unlock();
    }
  }


  /**
   * Returns the number of elements in this list.
   *
   * @return the number of elements in this list
   */
  @Override
  public int size() {
    return array.length;
  }


  /**
   * Returns a {@link Spliterator} over the elements in this list.
   *
   * <p>
   * The {@code Spliterator} reports {@link Spliterator#IMMUTABLE},
   * {@link Spliterator#ORDERED}, {@link Spliterator#SIZED}, and
   * {@link Spliterator#SUBSIZED}.
   *
   * <p>
   * The spliterator provides a snapshot of the state of the list when the
   * spliterator was constructed. No synchronization is needed while operating
   * on the spliterator.
   *
   * @return a {@code Spliterator} over the elements in this list
   * @since 1.8
   */
  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(array, Spliterator.IMMUTABLE | Spliterator.ORDERED);
  }


  @Override
  public List<E> subList(int arg0, int arg1) {
    throw new UnsupportedOperationException();
  }


  /**
   * Returns an array containing all of the elements in this list in proper
   * sequence (from first to last element).
   *
   * <p>
   * The returned array will be "safe" in that no references to it are
   * maintained by this list. (In other words, this method must allocate a new
   * array). The caller is thus free to modify the returned array.
   *
   * <p>
   * This method acts as bridge between array-based and collection-based APIs.
   *
   * @return an array containing all the elements in this list
   */
  @Override
  public Object[] toArray() {
    Object[] elements = array;
    return Arrays.copyOf(elements, elements.length);
  }


  /**
   * Returns an array containing all of the elements in this list in proper
   * sequence (from first to last element); the runtime type of the returned
   * array is that of the specified array. If the list fits in the specified
   * array, it is returned therein. Otherwise, a new array is allocated with the
   * runtime type of the specified array and the size of this list.
   *
   * <p>
   * If this list fits in the specified array with room to spare (i.e., the
   * array has more elements than this list), the element in the array
   * immediately following the end of the list is set to {@code null}. (This is
   * useful in determining the length of this list <i>only</i> if the caller
   * knows that this list does not contain any null elements.)
   *
   * <p>
   * Like the {@link #toArray()} method, this method acts as bridge between
   * array-based and collection-based APIs. Further, this method allows precise
   * control over the runtime type of the output array, and may, under certain
   * circumstances, be used to save allocation costs.
   *
   * <p>
   * Suppose {@code x} is a list known to contain only strings. The following
   * code can be used to dump the list into a newly allocated array of
   * {@code String}:
   *
   * <pre>
   * 
   * {
   *   &#64;code
   *   String[] y = x.toArray(new String[0]);
   * }
   * </pre>
   *
   * Note that {@code toArray(new Object[0])} is identical in function to
   * {@code toArray()}.
   *
   * @param a
   *          the array into which the elements of the list are to be stored, if
   *          it is big enough; otherwise, a new array of the same runtime type
   *          is allocated for this purpose.
   * @return an array containing all the elements in this list
   * @throws ArrayStoreException
   *           if the runtime type of the specified array is not a supertype of
   *           the runtime type of every element in this list
   * @throws NullPointerException
   *           if the specified array is null
   */
  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T a[]) {
    Object[] elements = array;
    int len = elements.length;
    if (a.length < len)
      return (T[])Arrays.copyOf(elements, len, a.getClass());
    else {
      System.arraycopy(elements, 0, a, 0, len);
      if (a.length > len)
        a[len] = null;
      return a;
    }
  }


  @Override
  public String toString() {
    return Arrays.toString(array);
  }

  private static final class COWIterator<E> implements ListIterator<E> {

    /** Snapshot of the array */
    private final Object[] snapshot;
    /** Index of element to be returned by subsequent call to next. */
    private int cursor;


    private COWIterator(Object[] elements, int initialCursor) {
      cursor = initialCursor;
      snapshot = elements;
    }


    @Override
    public void add(E e) {
      throw new UnsupportedOperationException();
    }


    @Override
    public boolean hasNext() {
      return cursor < snapshot.length;
    }


    @Override
    public boolean hasPrevious() {
      return cursor > 0;
    }


    @Override
    @SuppressWarnings("unchecked")
    public E next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      return (E)snapshot[cursor++];
    }


    @Override
    public int nextIndex() {
      return cursor;
    }


    @Override
    @SuppressWarnings("unchecked")
    public E previous() {
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }
      return (E)snapshot[--cursor];
    }


    @Override
    public int previousIndex() {
      return cursor - 1;
    }


    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }


    @Override
    public void set(E e) {
      throw new UnsupportedOperationException();
    }

  }

}
