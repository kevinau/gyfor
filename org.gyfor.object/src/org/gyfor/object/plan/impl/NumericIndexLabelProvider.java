package org.gyfor.object.plan.impl;

import org.gyfor.object.plan.IIndexLabelProvider;

public class NumericIndexLabelProvider implements IIndexLabelProvider {

  @Override
  public String getIndexLabel(int index) {
    return Integer.toString(index + 1);
  }

}
