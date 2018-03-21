package org.plcore.userio;

public class NumberedIndexLabelProvider implements IIndexLabelProvider {

  /**
   * Returns a numbered label, with the index offset by 1.  That is, it
   * returns a 1 based set of index labels.
   */
  @Override
  public String getIndexLabel (int index) {
    return Integer.toString(index + 1);
  }
}
