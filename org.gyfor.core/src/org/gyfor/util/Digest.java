package org.gyfor.util;

import java.io.Serializable;

public interface Digest extends Comparable<Digest>, Serializable {
  
  @Override
  public String toString();
  
}
