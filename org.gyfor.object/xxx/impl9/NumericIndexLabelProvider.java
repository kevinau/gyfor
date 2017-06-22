package org.gyfor.object.plan.impl9;

import org.gyfor.object.IIndexLabelProvider;

public class NumericIndexLabelProvider implements IIndexLabelProvider {

  @Override
  public String getIndexLabel(int index) {
    return Integer.toString(index + 1);
  }

}
