package org.gyfor.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** 
 * A very simple byte array buffer, designed for efficiency.
 *
 */
public class SimpleBuffer {

  byte[] data;
  
  int position = 0;
  
  
  public SimpleBuffer () {
    this.data = new byte[64];
  }
  
  
  public SimpleBuffer (int n) {
    this.data = new byte[n];
  }
  
  
  public SimpleBuffer (byte[] data) {
    this.data = data;
  }
  
  
  public byte[] ensureCapacity (int n) {
    int nx = position + n;
    if (nx > data.length) {
      nx = Math.max(position + n, data.length * 2);
      data = Arrays.copyOf(data, nx);
    }
    return data;
  }
  
  
  public String nextNulTerminatedString () {
    int n = data.length;
    int i = position;
    while (i < n && data[i] != 0) {
      i++;
    }
    if (i == n) {
      throw new IllegalArgumentException("No NUL byte from " + position);
    }
    String v = new String(data, position, i - position, StandardCharsets.UTF_8);
    position = i + 1;
    return v;
  }
  
  
  public void appendNulTerminatedString (String v) {
    byte[] vx = v.getBytes(StandardCharsets.UTF_8);
    byte[] data = ensureCapacity(vx.length + 1);

    System.arraycopy(vx, 0, data, position, vx.length);
    position += vx.length;
    data[position++] = 0;
  }
  
  
  public int next () {
    return data[position++];
  }
  
  
  public void append (int i) {
    data[position++] = (byte)i;
  }
  
  
  public byte[] bytes () {
    return data;
  }


  public int size () {
    return position;
  }

}

