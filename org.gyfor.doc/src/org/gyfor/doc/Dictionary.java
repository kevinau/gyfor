package org.gyfor.doc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * A map of words and associated index.
 * 
 * This class does not used ConcurrentHashMap because it is necessary to
 * synchronize the save method. ConcurrentHashMap does not allow external
 * locking. In addition, the overhead of ConcurrentHashMap is not justified in
 * this case.
 * 
 */
public class Dictionary {

  private Map<String, Integer> map = new HashMap<>(100);
  ////private int[] dictWordCounts = new int[0];
  ////private transient ArrayList<Integer> docWordCounts = new ArrayList<>(100);
  private transient boolean isReadOnly = false;

  public void setReadOnly(boolean isReadOnly) {
    this.isReadOnly = isReadOnly;
  }

  public boolean isReadOnly() {
    return isReadOnly;
  }

  public int get(String key) {
    Integer value = map.get(key);
    if (value == null) {
      return -1;
    } else {
      return value;
    }
  }

  public int size() {
    return map.size();
  }

  public int getWordIndex(String key) {
    Integer value = map.get(key);
    if (value == null) {
      return -1;
    } else {
      return value;
    }
  }

  
  public String getWord (int wordIndex) {
    // This is not efficient, but it is used for debugging only
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      if (entry.getValue() == wordIndex) {
        return entry.getKey();
      }
    }
    return "null(" + wordIndex + ")";
  }
 
  
  public int resolve(String word) {
    if (isReadOnly) {
      throw new IllegalStateException("Cannot resolve a word for a read-only dictionary");
    }
    Integer value = map.get(word);
    //if (isReadOnly) {
    //  if (value == null) {
    //    value = -1;
    //  }
    //} else {
      if (value == null) {
        value = map.size();
        map.put(word, value);
        ////docWordCounts.add(1);
      ////} else {
        ////docWordCounts.set(value, docWordCounts.get(value) + 1);
      }
    //}
    return value;
  }

  public void update(String word) {
    if (isReadOnly) {
      throw new IllegalStateException("Cannot update a read-only dictionary");
    }
    resolve(word);
  }

//  @Deprecated
//  public int getTwoPlusDictionaryWordCounts() {
//    int n = 0;
//    for (int i = 0; i < dictWordCounts.length; i++) {
//      if (dictWordCounts[i] >= 2) {
//        n++;
//      }
//    }
//    return n;
//  }
//
//  
//  @Deprecated
//  public void purgeSingleWords () {
//    if (isReadOnly) {
//      throw new IllegalStateException("Cannot purge single words from a read-only dictionary");
//    }
//    Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
//    while (it.hasNext()) {
//       Map.Entry<String, Integer> entry = it.next();
//       int i = entry.getValue();
//       if (dictWordCounts[i] <= 1) {
//         it.remove();
//       }
//    }
//    int i = 0;
//    for (Map.Entry<String, Integer> entry : map.entrySet()) {
//      entry.setValue(i);
//      i++;
//    }
//  }
//  
//  
//  public int[] getDictionaryWordCounts() {
//    return dictWordCounts;
//  }
//  
//
//  public void updateWordCounts() {
//    if (isReadOnly) {
//      throw new IllegalStateException("Cannot update word counts for a read-only dictionary");
//    }
//    dictWordCounts = Arrays.copyOf(dictWordCounts, map.size());
//    for (int i = 0; i < docWordCounts.size(); i++) {
//      int docCount = docWordCounts.get(i);
//      if (docCount > 0) {
//        dictWordCounts[i]++;
//      }
//    }
//    docWordCounts = new ArrayList<>(map.size());
//    int mapSize = map.size();
//    while (docWordCounts.size() < mapSize) {
//      docWordCounts.add(0);
//    }
//  }

  public void dump() {
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      System.out.println(entry.getKey() + "||" + entry.getValue());
    }
  }

  public void save(Path file) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(this);
    } catch (IOException ex) {
      throw new RuntimeException();
    }
  }

  public static Dictionary load(Path file) {
    Dictionary dictionary;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      dictionary = (Dictionary) ois.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
    return dictionary;
  }

}
