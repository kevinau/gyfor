package org.gyfor.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dictionary2 {

  private static String[] prime = {
      "are", 
      "best", 
      "buy", 
      "do", 
      "from", 
      "hey", 
      "me", 
      "this", 
      "to", 
      "want", 
      "you", 
      "i", 
      "a", 
      "air", 
      "come", 
      "friend", 
      "have", 
      "horse", 
      "in", 
      "is", 
      "it", 
      "my", 
      "nice", 
      "party", 
      "spring", 
      "the", 
      "today", 
      "tonight", 
      "weather",
  };


  private List<String> map = new ArrayList<>(100);
  
  
  public Dictionary2 () {
    map.addAll(Arrays.asList(prime));
  }


  public List<String> phrases() {
    return map;
  }
  
  
  public int resolve(String phrase) {
    int value = map.indexOf(phrase.toLowerCase());
    if (value == -1) {
      value = map.size();
      map.add(phrase);
    }
    //long crc = CRC64DigestFactory.getCRCValue(phrase);
//    Integer value = map.get(phrase);
  //  if (value == null) {
    //  value = map.size();
      //map.put(phrase, value);
  //  }
    return value + 1;
  }
  
  
  public int indexOf(String phrase) {
    return map.indexOf(phrase.toLowerCase()) + 1;
  }
  
  
  public int size() {
    return map.size();
  }
  
}
