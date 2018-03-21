package org.plcore.userio.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.plcore.userio.INode;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.PathParser;
import org.plcore.userio.path.Trail;
import org.plcore.userio.plan.IContainerPlan;


public abstract class ContainerModel extends NodeModel implements IContainerModel {

  // TODO change the private back to protected or something
  public final IValueReference valueRef;
  private final List<ContainerChangeListener> containerChangeListeners = new ArrayList<>();
  private final Map<Integer, INodeModel> nodesById = new HashMap<>();
  
  
  public ContainerModel(ModelFactory modelFactory, IValueReference valueRef, IContainerPlan containerPlan) {
    super(modelFactory, containerPlan);
    this.valueRef = valueRef;
  }

  
  @Override
  public <T> T getValue() {
    return valueRef.getValue();
  }
  
  @Override
  public String getValueRefName() {
    return valueRef.getName();
  }
  

  @Override
  public void addContainerChangeListener(ContainerChangeListener x) {
    containerChangeListeners.add(x);
  }

  @Override
  public void removeContainerChangeListener(ContainerChangeListener x) {
    containerChangeListeners.remove(x);
  }

  
  @Override
  public void fireChildAdded(IContainerModel parent, INodeModel node) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childAdded(parent, node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireChildAdded(parent, node);
    }
  }

  
  @Override
  public void fireChildRemoved(IContainerModel parent, INodeModel node) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childRemoved(parent, node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireChildRemoved(parent, node);
    }
  }

  
  @Override
  public List<INodeModel> selectNodeModels(String expr) {
    return selectNodeModels(PathParser.parse(expr));
  }

  
  @Override
  public List<INodeModel> selectNodeModels(IPathExpression pathExpr) {
    List<INodeModel> found = new ArrayList<>();
    pathExpr.matches((INodeModel)this, (Trail)null, new Consumer<INode>() {
      @Override
      public void accept(INode model) {
        found.add((INodeModel)model);
      }
    });
    return found;
  }

  
  @Override
  public List<IItemModel> selectItemModels(String expr) {
    return selectItemModels(PathParser.parse(expr));

  }

  
  @Override
  public List<IItemModel> selectItemModels(IPathExpression pathExpr) {
    List<IItemModel> found = new ArrayList<>();
    pathExpr.matches(this, null, new Consumer<INode>() {
      @Override
      public void accept(INode model) {
        if (model instanceof IItemModel) {
          found.add((IItemModel)model);
        }
      }
    });
    return found;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodeModel> X selectNodeModel(String expr) {
    List<INodeModel> found = selectNodeModels(expr);
    switch (found.size()) {
    case 0 :
      throw new IllegalArgumentException("No node models matching: " + expr);
    case 1 :
      return (X)found.get(0);
    default :
      throw new IllegalArgumentException(found.size() + " node models matching: " + expr + ". I.e., more than one");
    }
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodeModel> X selectNodeModel(int id) {
    INodeModel found = nodesById.get(id);
    if (found == null) {
      throw new IllegalArgumentException("No node model for id: " + id);
    }
    return (X)found;
  }

  
  @Override
  public IItemModel selectItemModel(String expr) {
    List<IItemModel> found = selectItemModels(expr);
    switch (found.size()) {
    case 0 :
      throw new IllegalArgumentException("No item models matching: " + expr);
    case 1 :
      return found.get(0);
    default :
      throw new IllegalArgumentException(found.size() + " item models matching: " + expr + ". I.e., more than one");
    }
  }

  
  @Override
  public void addById (INodeModel nodeModel) {
    int id = nodeModel.getNodeId();
    nodesById.put(id, nodeModel);
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodeModel> X getById (int id) {
    return (X)nodesById.get(id);
  }
  
  
  @Override
  public String getName() {
    return getPlan().getName();
  }

}
