package org.gyfor.object.model;

import org.gyfor.object.UserEntryException;


public class ItemEventAdapter extends EffectiveEntryModeAdapter implements ItemEventListener {

  @Override
  public void valueEqualityChange(INodeModel node) {
  }

  @Override
  public void sourceEqualityChange(INodeModel node) {
  }

  @Override
  public void errorCleared(INodeModel node) {
  }

  @Override
  public void errorNoted(INodeModel node, UserEntryException ex) {
  }

  @Override
  public void valueChange(INodeModel node) {
  }

  @Override
  public void comparisonBasisChange(INodeModel node) {
  }

  @Override
  public String getOrigin() {
    return "unknown";
  }

  @Override
  public void sourceChange(INodeModel node) {
  }

}
