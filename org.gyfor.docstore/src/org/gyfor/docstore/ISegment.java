package org.gyfor.docstore;

public interface ISegment extends Comparable<ISegment> {

  /**
   * Returns the word index of this Segment.  The word index will be:
   * <ul><li>0 to (n - 1), where n is the number of words in the Dictionary.</li>
   * <li>-1 if the Segment word is not in the Dictionary.</li>
   * </ul>
   * @return
   */
  //public int getWordIndex();

  @Override
  public String toString();

  public int getPageIndex();
  
  public int getSegmentId();
  
  public float getX0();

  public float getY0();

  public float getX1();

  public float getY1();

  public float getWidth();

  public float getHeight();

  public float getFontSize();
  
  public String getText();

  public SegmentType getType();
  
  public Object getValue();
  
  public void setFieldName(String name);
  
  public String getFieldName();
  
  public boolean overlapsHorizontally (ISegment other);
  
  public boolean overlapsVertically (ISegment other);
  
  public static boolean isDiscardable(String text) {
    char[] chars = text.toCharArray();
    for (char c : chars) {
      if (Character.isISOControl(c)) {
        return true;
      }
    }
    for (char c : chars) {
      if (Character.isLetterOrDigit(c) || Character.getType(c) == Character.CURRENCY_SYMBOL) {
        return false;
      }
    }
    return true;
  }

  public void updateDictionary(Dictionary dictionary);

  public void scaleBoundingBox(double factor);

  public void resolveWordIndex(Dictionary dictionary);

}