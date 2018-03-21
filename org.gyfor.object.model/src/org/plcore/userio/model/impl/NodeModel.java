package org.plcore.userio.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.plcore.type.UserEntryException;
import org.plcore.userio.EntryMode;
import org.plcore.userio.model.EffectiveEntryMode;
import org.plcore.userio.model.EffectiveEntryModeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ItemEventListener;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.plan.INodePlan;

public abstract class NodeModel implements INodeModel {

  private final ModelFactory modelFactory;
  private final int nodeId;

  private IContainerModel parent;
  private INodePlan nodePlan;
  
  private EntryMode entryMode = EntryMode.UNSPECIFIED;
  private EffectiveEntryMode effectiveEntryMode = EffectiveEntryMode.ENABLED;
  
  private List<EffectiveEntryModeListener> effectiveEntryModeListeners = new ArrayList<>();
  private List<ItemEventListener> itemEventListeners = new ArrayList<>();


  
  @Override
  public abstract void syncValue(Object value);
 
  
  @Override
  public void setParent (IContainerModel parent) {
    if (parent == null && !(this instanceof IEntityModel)) {
      throw new IllegalArgumentException("Parent is null and node is not an IEntityModel");
    }
    this.parent = parent;
    if (parent != null) {
      parent.addById(this);
    }
  }
  
  
  @Override
  public IContainerModel getParent() {
    return parent;
  }
  
  
  @Override
  public abstract <T> T getValue();
  
  
  protected NodeModel (ModelFactory modelFactory, INodePlan nodePlan) {
    this.modelFactory = modelFactory;
    this.nodeId = modelFactory.getNodeId();
    this.nodePlan = nodePlan;
  }
  

  protected INodeModel buildNodeModel (IContainerModel parent, IValueReference valueRef, INodePlan nodePlan) {
    INodeModel node = modelFactory.buildNodeModel(valueRef, nodePlan);
    node.setParent(parent);
    node.setEntryMode(nodePlan.getEntryMode());
    return node;
  }

  
  @Override
  public int getNodeId() {
    return nodeId;
  }
  
  
  @Override
  public void addEffectiveEntryModeListener (EffectiveEntryModeListener x) {
    effectiveEntryModeListeners.add(x);
  }
  
  
  @Override
  public void removeEffectiveEntryModeListener (EffectiveEntryModeListener x) {
    effectiveEntryModeListeners.remove(x);
  }

  
  /**
   * Add a ItemChangeListener.  
   */
  @Override
  public void addItemEventListener (ItemEventListener x) {
    itemEventListeners.add(x);
  }
  
  
  /**
   * Remove a ItemChangeListener.  
   */
  @Override
  public void removeItemEventListener (ItemEventListener x) {
    itemEventListeners.remove(x);
  }
  
  
  @Override
  public void setEntryMode (EntryMode entryMode) {
    this.entryMode = entryMode;

    EffectiveEntryMode parentMode;
    if (parent == null) {
      // Top level node (IEntity node)
      parentMode = EffectiveEntryMode.toEffective(entryMode);
    } else {
      parentMode = parent.getEffectiveEntryMode();
    }
    updateEffectiveEntryMode (parentMode);
  }

  
  @Override
  public void updateEffectiveEntryMode (EffectiveEntryMode parentMode) {
    EffectiveEntryMode newEffectiveEntryMode = EffectiveEntryMode.getEffective(parentMode, entryMode);
    if (newEffectiveEntryMode != effectiveEntryMode) {
      EffectiveEntryMode priorMode = effectiveEntryMode;
      effectiveEntryMode = newEffectiveEntryMode;
      fireEffectiveModeChange(this, priorMode);
      
      for (INodeModel child : getContainerNodes()) {
        child.updateEffectiveEntryMode (effectiveEntryMode);
      }     
    }
  }
  
  
  @Override
  public EffectiveEntryMode getEffectiveEntryMode() {
    return effectiveEntryMode;
  }
  
  
  @Override
  public void fireEffectiveModeChange (INodeModel node, EffectiveEntryMode priorMode) {
    for (EffectiveEntryModeListener x : effectiveEntryModeListeners) {
      x.effectiveModeChanged(node, priorMode);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireEffectiveModeChange(node, priorMode);
    }
  }

  
  @Override
  public void fireErrorNoted (INodeModel node, UserEntryException ex) {
    for (ItemEventListener x : itemEventListeners) {
      x.errorNoted(node, ex);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireErrorNoted(node, ex);
    }
  }
  
  
  @Override
  public void fireErrorCleared (INodeModel node) {
    for (ItemEventListener x : itemEventListeners) {
      x.errorCleared(node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireErrorCleared(node);
    }
  }
  
  
  @Override
  public void fireSourceChange (INodeModel node) {
    for (ItemEventListener x : itemEventListeners) {
      x.sourceChange(node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireSourceChange(node);
    }
  }
  
  
  @Override
  public void fireSourceEqualityChange (INodeModel node, boolean equal) {
    for (ItemEventListener x : itemEventListeners) {
      x.sourceEqualityChange(node, equal);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireSourceEqualityChange(node, equal);
    }
  }
  
  
  @Override
  public void fireValueChange (INodeModel node) {
    for (ItemEventListener x : itemEventListeners) {
      x.valueChange(node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireValueChange(node);
    }
  }
  
  
  @Override
  public void fireValueEqualityChange (INodeModel node, boolean equal) {
    for (ItemEventListener x : itemEventListeners) {
      x.valueEqualityChange(node, equal);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireValueEqualityChange(node, equal);
    }
  }
  
  
  @Override
  public void fireComparisonBasisChange (INodeModel node) {
    for (ItemEventListener x : itemEventListeners) {
      x.comparisonBasisChange(node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireComparisonBasisChange(node);
    }
  }
  
  
  @Override
  public abstract void dump(int level);
 
  
  protected void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodePlan> X getPlan() {
    return (X)nodePlan;
  }
  
  
  @Override
  public String getName() {
    return nodePlan.getName();
  }
  
  
  protected static void buildModelTrail (INodeModel model, List<INodeModel> trail) {
    IContainerModel parent = model.getParent();
    if (parent != null) {
      buildModelTrail(parent, trail);
    }
    trail.add(model);
  }
  

  @Override
  public String getQName () {
    StringBuilder builder = new StringBuilder();
    buildQName(getParentEntity(), builder);
    return builder.toString();
  }

  
  @Override
  public String getQName (IContainerModel relativeTo) {
    StringBuilder builder = new StringBuilder();
    buildQName(relativeTo, builder);
    String qname = builder.toString();
    if (!qname.startsWith("/")) {
      throw new RuntimeException("Relative QName should start with a / at this point");
    }
    return qname.substring(1);
  }
  
  
  protected void buildQName(IContainerModel top, StringBuilder builder) {
    if (getParent() != top) {
      ((NodeModel)getParent()).buildQName(top, builder);
    }
    builder.append('/');
    builder.append(getValueRefName());
  }
  
  
  protected void buildQName(StringBuilder builder) {
    if (getParent() != null) {
      ((NodeModel)getParent()).buildQName(builder);
    }
    builder.append('/');
    builder.append(getValueRefName());
  }
  
  
  protected void buildQualifiedPlanName(StringBuilder buffer) {
    if (parent != null) {
      ((NodeModel)parent).buildQualifiedPlanName(buffer);
      if (buffer.length() > 0) {
        buffer.append('.');
      }
      buffer.append(getName());
    }
  }
  
  
  @Override
  public String getQualifiedPlanName() {
    StringBuilder buffer = new StringBuilder();
    buildQualifiedPlanName(buffer);
    return buffer.toString();
  }
  
  
  @Override
  public IEntityModel getParentEntity() {
    return parent.getParentEntity();
  }

  
  @Override
  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after) {
    before.accept(this);
    after.accept(this);
  }
  
  
  @Override
  public void walkItems(Consumer<IItemModel> consumer) {
  }

  
  @Override
  public boolean matches(INodeModel startingPoint, IPathExpression expr) {
    boolean[] result = new boolean[1];
    expr.getNext().matches(startingPoint, this, result);
    return result[0];
  }

}
