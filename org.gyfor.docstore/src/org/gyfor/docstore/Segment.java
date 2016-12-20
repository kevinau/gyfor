package org.gyfor.docstore;

import java.io.Serializable;

import com.sleepycat.persist.model.Persistent;


@Persistent
public class Segment implements Serializable, ISegment {

  private static final long serialVersionUID = 1L;

//  private static Pattern titlePattern = Pattern.compile(" ");

  // Zero based page number
  private int pageIndex;
  
  private float x0;
  private float y0;
  private float x1;
  private float y1;
  private float fontSize;
  private String word;
  private SegmentType type;
  private Object value;
  
//  @Deprecated
//  Segment(PartialSegment old) {
//    this.x0 = old.getX0();
//    this.y0 = old.getY0();
//    this.x1 = old.getX1();
//    this.y1 = old.getY1();
//    this.word = old.getWord();
//  }

  Segment() {
  }
  
  
  Segment(int pageIndex, float x0, float y0, float x1, float y1, float fontSize, String word, SegmentType type, Object value) {
    if (!type.isRawText()) {
      if (value == null) {
        throw new IllegalArgumentException("Value cannot be null for non-text types");
      }
    }
    this.pageIndex = pageIndex;
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.fontSize = fontSize;
    this.word = word;
    this.type = type;
    this.value = value;
  }

//  @Deprecated
//  Segment(String title) {
//    String[] x = titlePattern.split(title);
//    x0 = Float.parseFloat(x[1]);
//    y0 = Float.parseFloat(x[2]);
//    x1 = Float.parseFloat(x[3]);
//    y1 = Float.parseFloat(x[4]);
//    this.word = null;
//  }

  @Override
  public void resolveWordIndex(Dictionary dictionary) {
    dictionary.resolve(word);
  }

  @Override
  public void updateDictionary(Dictionary dictionary) {
    dictionary.update(word);
  }

  @Override
  public void scaleBoundingBox(double factor) {
    x0 = (float)(x0 * factor);
    y0 = (float)(y0 * factor);
    x1 = (float)(x1 * factor);
    y1 = (float)(y1 * factor);
  }

  @Override
  public int compareTo (ISegment other) {
    float thisY = (y0 + y1) / 2;
    float otherY = (other.getY0() + other.getY1()) / 2;
    if (thisY < otherY) {
      return -1;
    } else if (thisY > otherY) {
      return +1;
    }
    float thisX = (x0 + x1) / 2;
    float otherX = (other.getX0() + other.getX1()) / 2;
    if (thisX < otherX) {
      return -1;
    } else if (thisX > otherX) {
      return +1;
    }
    return 0;
  }
  
  @Override
  public String toString() {
    StringBuilder s = new StringBuilder("[" + pageIndex + ": " + x0 + "," + y0 + "," + x1 + "," + y1 + ": " + word + "@" + type);
    s.append("]");
    return s.toString();
  }

  
  @Override
  public int getPageIndex() {
    return pageIndex;
  }
  
  
  @Override
  public float getX0() {
    return x0;
  }

  @Override
  public float getY0() {
    return y0;
  }

  @Override
  public float getX1() {
    return x1;
  }

  @Override
  public float getY1() {
    return y1;
  }

  @Override
  public float getWidth() {
    return x1 - x0;
  }

  @Override
  public float getHeight() {
    return y1 - y0;
  }

  @Override
  public float getFontSize() {
    return fontSize;
  }
  
  @Override
  public String getText() {
    return word;
  }

  @Override
  public SegmentType getType() {
    return type;
  }
  
  @Override
  public Object getValue() {
    return value;
  }
  

  @Override
  public boolean overlapsHorizontally (ISegment other) {
    if (other.getY1() < y0) {
      // there can be no overlap
      return false;
    } else if (other.getY0() > y1) {
      // there can be no overlap
      return false;
    } else {
      return true;
    }
  }
  
  
  @Override
  public boolean overlapsVertically (ISegment other) {
    if (other.getX1() < x0) {
      // there can be no overlap
      return false;
    } else if (other.getX0() > x1) {
      // there can be no overlap
      return false;
    } else {
      return true;
    }
  }

}
