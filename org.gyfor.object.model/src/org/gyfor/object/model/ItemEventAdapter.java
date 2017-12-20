package org.gyfor.object.model;

import org.gyfor.object.UserEntryException;


public class ItemEventAdapter extends EffectiveEntryModeAdapter implements ItemEventListener {

  @Override
  public void valueEqualityChange(IItemModel model) {
  }

  @Override
  public void sourceEqualityChange(IItemModel model) {
  }

  @Override
  public void errorCleared(IItemModel model) {
  }

  @Override
  public void errorNoted(IItemModel model, UserEntryException ex) {
  }

  @Override
  public void valueChange(IItemModel model) {
  }

  @Override
  public void comparisonBasisChange(IItemModel model) {
  }

  @Override
  public String getOrigin() {
    return "unknown";
  }

  @Override
  public void sourceChange(IItemModel model) {
  }

}
