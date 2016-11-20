package org.gyfor.object.model;

import org.gyfor.object.plan.INodePlan;

public abstract class NodeModel {

  private final RootModel rootModel;
  private final NodeModel parent;
  private final int id;
  
  
  public NodeModel (RootModel rootModel, NodeModel parent, int id) {
    this.rootModel = rootModel;
    this.parent = parent;
    this.id = id;
  }
  
  
  public int getId() {
    return id;
  }
  
  
  protected RootModel getRoot() {
    return rootModel;
  }
  
  
  protected NodeModel getParent() {
    return parent;
  }
  
  
  private static void buildCanonicalName (NodeModel nodeModel, StringBuilder builder, boolean[] isTop) {
    if (nodeModel.parent == null) {
      if (nodeModel instanceof EntityModel) {
        builder.append(((EntityModel)nodeModel).getPlan().getClassName());
      } else {
        throw new RuntimeException("Parent of " + nodeModel + " is null, but it is not an entity model");
      }
      isTop[0] = true;
    } else {
      buildCanonicalName(nodeModel.parent, builder, isTop);
      if (isTop[0]) {
        builder.append('#');
      } else {
        builder.append('.');
      }
      builder.append(nodeModel.getPlan().getName());
    }
  }
  
  
  public String getCanonicalName() {
    StringBuilder builder = new StringBuilder();
    boolean[] isTop = new boolean[1];
    buildCanonicalName(this, builder, isTop);
    return builder.toString();
  }

  
  protected void fireChildAdded (NodeModel node) {
    rootModel.fireChildAdded(parent, node);
  }
  
  
  protected void fireChildRemoved (NodeModel node) {
    rootModel.fireChildRemoved(parent, node);
  }


  public abstract INodePlan getPlan();
  
  
  public abstract void setValue (Object value);
  
  
  public abstract Object getValue ();
  

  @Override
  public int hashCode() {
    return id;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof NodeModel)) {
      return false;
    }
    NodeModel other = (NodeModel)obj;
    return id == other.id;
  }
  
  
  public boolean isContainer () {
    return false;
  }
  
  public boolean isNameMapped () {
    return false;
  }
  
  public boolean isRepeating () {
    return false;
  }
  
  public boolean isItem () {
    return false;
  }
  
}
