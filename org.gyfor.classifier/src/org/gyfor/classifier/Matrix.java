package org.gyfor.classifier;

import java.util.Arrays;

public class Matrix {

  private static final int PADDING = 20;

  private Vector[] elements;

  
  public Matrix () {
    elements = new Vector[0];
  }

  
  public Matrix (Vector[] values) {
    elements = values;
  }
  
  
  public Vector get(int i) {
    if (i < elements.length) {
      return elements[i];
    } else {
      return new Vector();
    }
  }


  public double get(int i, int j) {
    if (i < elements.length) {
      return elements[i].get(j);
    } else {
      return 0;
    }
  }


  public void set(int i, int j, double value) {
    if (i >= elements.length) {
      synchronized (this) {
        int n = elements.length;
        elements = Arrays.copyOf(elements, i + PADDING);
        while (n < i + PADDING) {
          elements[n] = new Vector();
        }
      }
    }
    elements[i].set(j, value);
  }


  public void set(int i, Vector value) {
    if (i >= elements.length) {
      synchronized (this) {
        int n = elements.length;
        elements = Arrays.copyOf(elements, i + PADDING);
        while (n < i + PADDING) {
          elements[n] = new Vector();
        }
      }
    }
    elements[i] = value;
  }


  public void set(Vector[] values) {
    synchronized (this) {
      elements = values;
    }
  }
  
  
  public void clear() {
    synchronized (this) {
      elements = new Vector[0];
    }
  }

}
