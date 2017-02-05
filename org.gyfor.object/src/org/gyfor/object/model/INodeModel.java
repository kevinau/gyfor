package org.gyfor.object.model;

import java.util.List;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.type.IType;


public interface INodeModel {

  public IContainerModel getParent();
  
  public List<INodeModel> getChildNodes ();

  public int getNodeId();

  public String getCanonicalName ();

  public <X extends INodePlan> X getPlan ();

  public void setValue (Object value);

  public <X> X getValue ();
  
  public IValueReference getValueRef ();

  public void dump(int level);

  public IType<?> getType();

  public void setEntryMode(EntryMode entryMode);

  public void updateEffectiveEntryMode(EffectiveEntryMode parent);

  public EffectiveEntryMode getEffectiveEntryMode();

  public EntryMode getEntryMode();

  public void addEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public void removeEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public void syncEventsWithNode();

}