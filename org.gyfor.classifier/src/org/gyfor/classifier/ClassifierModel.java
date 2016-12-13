package org.gyfor.classifier;

import org.apache.mahout.math.Arrays;

public class ClassifierModel {

  private static class Vector {
    
    private static final int PADDING = 20;
    
    private double[] elements = new double[0];
    
    public double get (int i) {
      if (i < elements.length) {
        return elements[i];
      } else {
        return 0.0;
      }
    }
    
    
    public void set (int i, double value) {
      if (i >= elements.length) {
        synchronized (this) {
          elements = Arrays.copyOf(elements, i + PADDING);
        }
      }
      elements[i] = value;
    }
    
    
    public void clear () {
     synchronized (this) {
       elements = new double[0];
     }
    }
    
  }
  
  
  
}
