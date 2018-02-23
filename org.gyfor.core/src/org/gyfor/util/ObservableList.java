package org.gyfor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;


public class ObservableList<E> implements List<E> {

  private final List<E> proxy;

  private final List<ListChangeListener<E>> listeners = new CopyOnWriteArrayList<>();

  public ObservableList() {
    proxy = new ArrayList<>();
  }

  public ObservableList(int n) {
    proxy = new ArrayList<>(n);
  }

  public void addListChangeListener(ListChangeListener<E> x) {
    listeners.add(x);
  }

  public void removeListChangeListener(ListChangeListener<E> x) {
    listeners.remove(x);
  }

  private void fireElementAdded(E elem) {
    for (ListChangeListener<E> x : listeners) {
      x.elementAdded(elem);
    }
  }

  private void fireElementChanged(E elem) {
    for (ListChangeListener<E> x : listeners) {
      x.elementChanged(elem);
    }
  }

  private void fireElementRemoved(E elem) {
    for (ListChangeListener<E> x : listeners) {
      x.elementRemoved(elem);
    }
  }

  @Override
  public boolean add(E elem) {
    proxy.add(elem);
    fireElementAdded(elem);
    return true;
  }

  @Override
  public void add(int index, E elem) {
    proxy.add(index, elem);
    fireElementAdded(elem);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    for (E elem : c) {
      add(elem);
    }
    return c.size() > 0;
  }

  public boolean addAll(E[] c) {
    for (E elem : c) {
      add(elem);
    }
    return c.length > 0;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean addAll(int index, Collection c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Object elem) {
    return proxy.contains(elem);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return proxy.containsAll(c);
  }

  @Override
  public E get(int index) {
    return proxy.get(index);
  }

  @Override
  public int indexOf(Object elem) {
    return proxy.indexOf(elem);
  }

  @Override
  public boolean isEmpty() {
    return proxy.isEmpty();
  }

  @Override
  public Iterator<E> iterator() {
    return proxy.iterator();
  }

  @Override
  public int lastIndexOf(Object elem) {
    return proxy.lastIndexOf(elem);
  }

  @Override
  public ListIterator<E> listIterator() {
    return proxy.listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return proxy.listIterator(index);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(Object elem) {
    boolean found = proxy.remove(elem);
    if (found) {
      fireElementRemoved((E)elem);
    }
    return found;
  }

  @Override
  public E remove(int index) {
    E elem = proxy.remove(index);
    fireElementRemoved(elem);
    return elem;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public E set(int index, E elem) {
    E old = proxy.set(index, elem);
    if (!old.equals(elem)) {
      fireElementChanged(elem);
    }
    return old;
  }

  @Override
  public int size() {
    return proxy.size();
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  @Override
  public E[] toArray() {
    return (E[])proxy.toArray();
  }

  @SuppressWarnings("unchecked")
  @Override
  public E[] toArray(Object[] a) {
    return (E[])proxy.toArray(a);
  }

}
