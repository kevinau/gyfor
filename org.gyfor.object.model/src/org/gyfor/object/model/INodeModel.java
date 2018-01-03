package org.gyfor.object.model;

import java.util.Collection;
import java.util.function.Consumer;

import org.gyfor.object.EntryMode;
import org.gyfor.object.INode;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.plan.INodePlan;

public interface INodeModel extends INode {

  public <X extends INodeModel> Collection<X> getContainerNodes();
  
  public int getNodeId();
  
  public void syncValue(Object value);

  public void setParent(IContainerModel parent);

  public <X extends INodePlan> X getPlan ();

  public <X> X getValue();
  
  public void setEntryMode(EntryMode entryMode);

  public void updateEffectiveEntryMode(EffectiveEntryMode parent);

  public EffectiveEntryMode getEffectiveEntryMode();

  @Override
  public String getName();
  
  public void buildQualifiedNamePart(StringBuilder builder, boolean[] isFirst, int[] repeatCount);

  public void addEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public void removeEffectiveEntryModeListener(EffectiveEntryModeListener x);

  public void addItemEventListener(ItemEventListener x);

  public void removeItemEventListener(ItemEventListener x);

  public default void dump() {
    dump(0);
  }

  public void dump(int level);

  public IContainerModel getParent();

  public String getQualifiedName();
  
  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after);

  public void fireEffectiveModeChange(INodeModel node, EffectiveEntryMode priorMode);

  public void fireErrorNoted(INodeModel node, UserEntryException ex);

  public void fireErrorCleared(INodeModel node);

  public void fireSourceChange(INodeModel node);

  public void fireSourceEqualityChange(INodeModel node);

  public void fireValueChange(INodeModel node);

  public void fireValueEqualityChange(INodeModel node);

  public void fireComparisonBasisChange(INodeModel node);

  public String getQualifiedPlanName();

  public IEntityModel getParentEntity();

}