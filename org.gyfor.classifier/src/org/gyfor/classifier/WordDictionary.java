package org.gyfor.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordDictionary {

  private Map<String, Integer> wordMap = new HashMap<>(500);
  private List<String> reverseMap = new ArrayList<>();
  
  public int queryWordIndex (String word) {
    Integer i = wordMap.get(word);
    if (i == null) {
      return -1;
    } else {
      return i;
    }
  }
  
  
  public int getWordIndex (String word) {
    Integer i = wordMap.get(word);
    if (i == null) {
      i = wordMap.size();
      wordMap.put(word, i);
      reverseMap.add(word);
    }
    return i;
  }
  
  
  public void clear () {
    wordMap.clear();
    reverseMap.clear();
  }
  
  
  public String getWord (int index) {
    return reverseMap.get(index);
  }
  
  
  public int size() {
//    return 1500;
    return wordMap.size();
  }
  
}
