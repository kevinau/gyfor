package org.gyfor.object.model;

import org.gyfor.object.UserEntryException;


public class ItemEventAdapter extends EffectiveModeAdapter implements ItemEventListener {

  @Override
  public void valueEqualityChange(ItemModel model) {
  }

  @Override
  public void sourceEqualityChange(ItemModel model) {
  }

  @Override
  public void errorCleared(ItemModel model) {
  }

  @Override
  public void errorNoted(ItemModel model, UserEntryException ex) {
  }

  @Override
  public void valueChange(ItemModel model) {
  }

  @Override
  public void comparisonBasisChange(ItemModel model) {
  }

  @Override
  public String getOrigin() {
    return "unknown";
  }

  @Override
  public void sourceChange(ItemModel model) {
  }

}
