package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.gyfor.object.EntryMode;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.model.EffectiveEntryMode;
import org.gyfor.object.model.EffectiveEntryModeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IRepeatingModel;
import org.gyfor.object.model.ItemEventListener;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.INodePlan;

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
      x.effectiveModeChanged(this, priorMode);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireEffectiveModeChange(node, priorMode);
    }
  }

  
  @Override
  public void fireErrorNoted (INodeModel node, UserEntryException ex) {
    System.out.println("AAAAAAAAAAA fireErrorNoted " + node.getNodeId() + " " + ex);
    System.out.println("AAAAAAAAAAA fireErrorNoted " + itemEventListeners.size());
    for (ItemEventListener x : itemEventListeners) {
      x.errorNoted(node, ex);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      System.out.println("AAAAAAAAAAA fireErrorNoted up " + parentNode);

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
  public void fireSourceEqualityChange (INodeModel node) {
    for (ItemEventListener x : itemEventListeners) {
      x.sourceEqualityChange(node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireSourceEqualityChange(node);
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
  public void fireValueEqualityChange (INodeModel node) {
    for (ItemEventListener x : itemEventListeners) {
      x.valueEqualityChange(node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireValueEqualityChange(node);
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
      parentNode.fireValueEqualityChange(node);
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
  

  private String buildQualifiedName () {
    List<INodeModel> trail = new ArrayList<>();
    buildModelTrail (this, trail);
    
    StringBuilder builder1 = new StringBuilder();
    boolean isFirst = true;
    boolean isRepeating = false;
    
    int i = 0;
    for (INodeModel x : trail) {
      if (i == 0) {
        // Don't include the entity name
      } else {
        if (isRepeating) {
          builder1.append('[');
          builder1.append(x.getName());
          builder1.append(']');
          isRepeating = false;
        } else {
          if (!isFirst) {
            builder1.append('.');
          }
          builder1.append(x.getName());
          if (x instanceof IRepeatingModel) {
            isRepeating = true;
          }
          isFirst = false;
        }
      }
      i++;
    }
    return builder1.toString();
  }
  
  
  @Override
  public String getQualifiedName () {
    return buildQualifiedName();
  }
  
  
  private void buildQualifiedPlanName(StringBuilder buffer) {
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

}
