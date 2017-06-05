package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.EffectiveEntryMode;
import org.gyfor.object.model.EffectiveEntryModeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IValueReference;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.INodePlan;

public abstract class NodeModel implements INodeModel {

  private final ModelFactory modelFactory;
  private final int nodeId;

  private IContainerModel parent;
  private INodePlan nodePlan;
  
  private EntryMode entryMode = EntryMode.UNSPECIFIED;
  private EffectiveEntryMode effectiveEntryMode = EffectiveEntryMode.ENABLED;
  
  private List<EffectiveEntryModeListener> effectiveEntryModeListeners = new ArrayList<>();
  
  private String qualifiedName;
  
  
  @Override
  public abstract void syncValue(Object value);
 
  
  @Override
  public void setParent (IContainerModel parent) {
    if (parent == null && !(this instanceof IEntityModel)) {
      throw new IllegalArgumentException("Parent is null and node is not an IEntityModel");
    }
    this.parent = parent;
  }
  
  
  @Override
  public IContainerModel getParent() {
    return parent;
  }
  
  
  @Override
  public abstract <T> T getValue();
  
  
  public NodeModel (ModelFactory modelFactory, INodePlan nodePlan) {
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
      fireEffectiveModeChange(priorMode);
      
      for (INodeModel child : getChildNodes()) {
        child.updateEffectiveEntryMode (effectiveEntryMode);
      }     
    }
  }
  
  
  @Override
  public EffectiveEntryMode getEffectiveEntryMode() {
    return effectiveEntryMode;
  }
  
  
  private void fireEffectiveModeChange (EffectiveEntryMode priorMode) {
    for (EffectiveEntryModeListener x : effectiveEntryModeListeners) {
      x.effectiveModeChanged(this, priorMode);
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
    boolean[] isFirst = new boolean[1];
    int[] repeatCount = new int[1];
    for (INodeModel x : trail) {
      x.buildQualifiedNamePart(builder1, isFirst, repeatCount);
    }
    return builder1.toString();
  }
  
  
  @Override
  public String getQualifiedName () {
    if (qualifiedName == null) {
      qualifiedName = buildQualifiedName();
    }
    return qualifiedName;
  }
}
