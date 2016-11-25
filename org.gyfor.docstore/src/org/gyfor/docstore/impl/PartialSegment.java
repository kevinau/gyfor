package org.gyfor.docstore.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gyfor.docstore.Dictionary;

public class PartialSegment {

  public static class Nibble {

    private final float offset;
    private final float width;
    private final int chars;
    
    public Nibble (float offset, float width, String text) {
      this.offset = offset;
      this.width = width;
      this.chars = text.length();
    }
    
    public Nibble (float offset, float width, int chars) {
      this.offset = offset;
      this.width = width;
      this.chars = chars;
    }
    
    public Nibble (double offset, double width, String text) {
      this.offset = (float)offset;
      this.width = (float)width;
      this.chars = text.length();
    }
    
    @Override
    public String toString () {
      return "(" + offset + " " + width + " " + chars + ")";
    }
    
  }

  
  private static Pattern titlePattern = Pattern.compile(" ");

  private float x0;
  private float y0;
  private float x1;
  private float y1;
  private String word;
  private transient List<Nibble> nibbles;

  PartialSegment(PartialSegment old, String word) {
    this.x0 = old.x0;
    this.y0 = old.y0;
    this.x1 = old.x1;
    this.y1 = old.y1;
    this.word = word;
    this.nibbles = new ArrayList<>();
    this.nibbles.add(new Nibble(0, old.x1 - old.x0, word));
  }
  
  
  PartialSegment(float x0, float y0, float x1, float y1, String word) {
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.word = word;
    this.nibbles = new ArrayList<>();
  }


  public PartialSegment(String title) {
    String[] x = titlePattern.split(title);
    x0 = Float.parseFloat(x[1]);
    y0 = Float.parseFloat(x[2]);
    x1 = Float.parseFloat(x[3]);
    y1 = Float.parseFloat(x[4]);
    this.word = null;
  }

  // Next segment will have null text
  double aveCharWidth(PartialSegment nextSegment, String nextText) {
    double bboxWidths = (x1 - x0) + (nextSegment.x1 - nextSegment.x0);
    double n = word.length() + nextText.length();
    return bboxWidths / n;
  }

  void extendWide(PartialSegment next, String word) {
    y0 = Float.min(y0, next.y0);
    x1 = next.x1;
    y1 = Float.max(y1, next.y1);
    this.word += word;
    nibbles.add(new Nibble(next.x0 - x0, next.x1 - x0, word));
  }

  void extendWide(PartialSegment next, char gap, String word) {
    nibbles.add(new Nibble(x1 - x0, next.x0 - x1, 1));
    y0 = Float.min(y0, next.y0);
    x1 = next.x1;
    y1 = Float.max(y1, next.y1);
    this.word += gap + word;
    nibbles.add(new Nibble(next.x0 - x0, next.x1 - next.x0, word));
  }

  boolean overlaps(PartialSegment arg, String word) {
    double aveCharWidth = aveCharWidth(arg, word);
    return x1 + aveCharWidth * 1.2 >= arg.x0;
  }

  boolean almostAdjacent(PartialSegment arg, String word) {
    double aveCharWidth = aveCharWidth(arg, word);
    return x1 + aveCharWidth * 0.3 >= arg.x0;
  }

  void resolveWordIndex(Dictionary dictionary) {
    dictionary.resolve(word);
  }

  void add(Nibble nibble) {
    nibbles.add(nibble);
  }
  
  
  float adjustedX0 (int startChar) {
    int n = 0;
    for (Nibble nibble : nibbles) {
      if (n + nibble.chars > startChar) {
        return (float)(x0 + nibble.offset + (startChar - n) * nibble.width / nibble.chars);
      }
      n += nibble.chars;
    }
    throw new IllegalArgumentException("Start character index too large");
  }
  
  
  float adjustedX1 (int endChar) {
    int n = 0;
    Nibble lastNibble = null;
    for (Nibble nibble : nibbles) {
      if (n + nibble.chars > endChar) {
        return (float)(x0 + nibble.offset + (endChar - n) * nibble.width / nibble.chars);
      }
      lastNibble = nibble;
      n += nibble.chars;
    }
    return (float)(x0 + lastNibble.offset + lastNibble.width);
  }
  
  
  float getX0() {
    return x0;
  }
  

  float getY0() {
    return y0;
  }
  

  float getX1() {
    return x1;
  }
  

  float getY1() {
    return y1;
  }
  

  String getWord() {
    return word;
  }
  
  
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("[" + x0 + "," + y0 + "," + x1 + "," + y1 + ": " + word);
    if (nibbles != null) {
      for (Nibble n : nibbles) {
        s.append(n.toString());
      }
    }
    s.append("]");
    return s.toString();
  }

}
