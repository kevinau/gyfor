package org.plcore.userio.plan.impl;

import org.plcore.userio.IIndexLabelProvider;

public class NumericIndexLabelProvider implements IIndexLabelProvider {

  @Override
  public String getIndexLabel(int index) {
    return Integer.toString(index + 1);
  }

}
