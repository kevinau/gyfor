package org.plcore.docstore.parser.impl;

import java.util.HashMap;

public class WordMap extends HashMap<String, WordMap.Counter> {

  private static final long serialVersionUID = 1L;


  public static class Counter {
    private int count = 1;
    
    public void increment () {
      count++;
    }
    
    public int getCount () {
      return count;
    }
  }
  
  
  public WordMap () {
    super(100);
  }  
 
  
  public void addWord (byte[] buffer) {
    boolean target = false;
    for (int i = 0; i < buffer.length; i++) {
      char c = (char)buffer[i];
      if (Character.isDigit(c) || Character.isAlphabetic(c)) {
        target = true;
        break;
      }
    }
    if (target) {
      String w = new String(buffer).trim();
      Counter counter = get(w);
      if (counter == null) {
        put(w, new Counter());
      } else {
        counter.increment();
      }
    }
  }

}
