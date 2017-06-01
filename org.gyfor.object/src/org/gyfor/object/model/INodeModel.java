package org.gyfor.object.model;

import java.util.List;

import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.INodePlan;


public interface INodeModel {

  public IContainerModel getParent();
  
  public List<INodeModel> getChildNodes ();

  public int getNodeId();

  public String getCanonicalName ();

  public <X extends INodePlan> X getPlan ();

  public void setValue (Object value);

  public <X> X getValue ();
  
  public void setEntryMode(EntryMode entryMode);

  public void updateEffectiveEntryMode(EffectiveEntryMode parent);

  public EffectiveEntryMode getEffectiveEntryMode();

  public EntryMode getEntryMode();
  
  public String getName();

  public void addEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public void removeEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public void syncEventsWithNode();

  public void dump(int level);
  
}