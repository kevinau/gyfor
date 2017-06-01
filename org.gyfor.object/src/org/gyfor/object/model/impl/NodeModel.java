package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.EffectiveEntryMode;
import org.gyfor.object.model.EffectiveEntryModeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;

public abstract class NodeModel implements INodeModel {

  @SuppressWarnings("unused")
  private final IEntityModel entityModel;
  private final IContainerModel parent;
  private final int nodeId;
  
  private EntryMode entryMode = EntryMode.ENABLED;
  private EffectiveEntryMode effectiveEntryMode = EffectiveEntryMode.ENABLED;
  
  private List<EffectiveEntryModeListener> effectiveEntryModeListeners = new ArrayList<>();
  
  
  public NodeModel (IModelFactory modelFactory, IEntityModel entityModel, IContainerModel parent) {
    this.entityModel = entityModel;
    this.parent = parent;
    this.nodeId = modelFactory.getNodeId();
  }
  
  
  protected IEntityModel getEntity() {
    INodeModel node = this;
    while (!(node instanceof IEntityModel)) {
      node = node.getParent();
    }
    return (IEntityModel)node;
  }
  
  
  @Override
  public IContainerModel getParent() {
    return parent;
  }
  
  
  @Override
  public int getNodeId() {
    return nodeId;
  }
  
  
  @Override
  public abstract List<INodeModel> getChildNodes();
  
  
  private static void buildCanonicalName (INodeModel nodeModel, StringBuilder builder, boolean[] isTop) {
    if (nodeModel.getParent() == null) {
      if (nodeModel instanceof IEntityModel) {
        builder.append(((IEntityPlan<?>)nodeModel.getPlan()).getClassName());
      } else {
        throw new RuntimeException("Parent of " + nodeModel + " is null, but it is not an entity model");
      }
      isTop[0] = true;
    } else {
      buildCanonicalName(nodeModel.getParent(), builder, isTop);
      if (isTop[0]) {
        builder.append('#');
      } else {
        builder.append('.');
      }
      builder.append(nodeModel.getPlan().getName());
    }
  }
  
  
  @Override
  public String getCanonicalName() {
    StringBuilder builder = new StringBuilder();
    boolean[] isTop = new boolean[1];
    buildCanonicalName(this, builder, isTop);
    return builder.toString();
  }

  
  @Override
  public abstract <X extends INodePlan> X getPlan();
  
  
  @Override
  public abstract void setValue (Object value);
  
  
  @Override
  public abstract <X> X getValue ();

  
  @Override
  public void addEffectiveEntryModeListener (EffectiveEntryModeListener x) {
    effectiveEntryModeListeners.add(x);
  }
  
  
  @Override
  public void removeEffectiveEntryModeListener (EffectiveEntryModeListener x) {
    effectiveEntryModeListeners.remove(x);
  }
  

  @Override
  public void syncEventsWithNode () {
    fireEffectiveModeChange();
  }
  
  
  @Override
  public void setEntryMode (EntryMode entryMode) {
    if (this.entryMode != entryMode) {
      this.entryMode = entryMode;

      EffectiveEntryMode parentMode;
      if (parent == null) {
        // Top level node (IEntity node)
        parentMode = EffectiveEntryMode.toEffective(getEntryMode());
      } else {
        parentMode = parent.getEffectiveEntryMode();
      }
      updateEffectiveEntryMode (parentMode);
    }
  }

  
  @Override
  public void updateEffectiveEntryMode (EffectiveEntryMode parentMode) {
    EffectiveEntryMode newEffectiveEntryMode = EffectiveEntryMode.getEffective(parentMode, entryMode);
    if (newEffectiveEntryMode != effectiveEntryMode) {
      effectiveEntryMode = newEffectiveEntryMode;
      fireEffectiveModeChange();
      
      for (INodeModel child : getChildNodes()) {
        child.updateEffectiveEntryMode (effectiveEntryMode);
      }     
    }
  }
  
  
  @Override
  public EffectiveEntryMode getEffectiveEntryMode() {
    return effectiveEntryMode;
  }
  
  
  @Override
  public EntryMode getEntryMode() {
    return entryMode;
  }
  
  
  private void fireEffectiveModeChange () {
    for (EffectiveEntryModeListener x : effectiveEntryModeListeners) {
      x.effectiveModeChanged(this);
    }
  }
  
  
//  @Override
//  public int hashCode() {
//    return id;
//  }
//
//
//  @Override
//  public boolean equals(Object obj) {
//    if (this == obj) {
//      return true;
//    }
//    if (obj == null) {
//      return false;
//    }
//    if (!(obj instanceof NodeModel)) {
//      return false;
//    }
//    NodeModel other = (NodeModel)obj;
//    return id == other.id;
//  }

}
