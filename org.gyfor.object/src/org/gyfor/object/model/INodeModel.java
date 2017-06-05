package org.gyfor.object.model;

import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.INodePlan;

public interface INodeModel {

  public INodeModel[] getChildNodes();
  
  public int getNodeId();
  
  public void syncValue(Object value);

  public void setParent(IContainerModel parent);

  public <X extends INodePlan> X getPlan ();

  public <X> X getValue();
  
  public void setEntryMode(EntryMode entryMode);

  public void updateEffectiveEntryMode(EffectiveEntryMode parent);

  public EffectiveEntryMode getEffectiveEntryMode();

  public String getName();
  
  public void buildQualifiedNamePart(StringBuilder builder, boolean[] isFirst, int[] repeatCount);

  public void addEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public void removeEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public default void dump() {
    dump(0);
  }

  public void dump(int level);

  public IContainerModel getParent();

  public String getQualifiedName();

}