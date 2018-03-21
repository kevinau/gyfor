package org.plcore.userio.model;

import java.util.Collection;
import java.util.function.Consumer;

import org.plcore.type.UserEntryException;
import org.plcore.userio.EntryMode;
import org.plcore.userio.INode;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.plan.INodePlan;

public interface INodeModel extends INode {

  public Collection<? extends INodeModel> getContainerNodes();
  
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

  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after);

  public void walkItems(Consumer<IItemModel> consumer);

  public void fireEffectiveModeChange(INodeModel node, EffectiveEntryMode priorMode);

  public void fireErrorNoted(INodeModel node, UserEntryException ex);

  public void fireErrorCleared(INodeModel node);

  public void fireSourceChange(INodeModel node);

  public void fireSourceEqualityChange(INodeModel node, boolean equal);

  public void fireValueChange(INodeModel node);

  public void fireValueEqualityChange(INodeModel node, boolean equal);

  public void fireComparisonBasisChange(INodeModel node);
  
  public IEntityModel getParentEntity();

  public String getQName();

  public String getQName(IContainerModel top);

  public String getValueRefName();

  public String getQualifiedPlanName();

  public boolean matches(INodeModel startingPoint, IPathExpression expr);

}