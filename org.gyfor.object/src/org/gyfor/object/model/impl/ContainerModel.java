package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.path.IPathExpression;
import org.gyfor.object.model.path.ParseException;
import org.gyfor.object.model.path.PathParser;

public abstract class ContainerModel extends NodeModel implements IContainerModel {

  private List<ContainerChangeListener> containerChangeListeners = new ArrayList<>();
  
  
  protected ContainerModel(IModelFactory modelFactory, IEntityModel entityModel, IContainerModel parent) {
    super(modelFactory, entityModel, parent);
  }

  @Override
  public String toString () {
    return "ContainerModel(" + getNodeId() + ")";
  }

  
  @Override
  public void addContainerChangeListener (ContainerChangeListener x) {
    containerChangeListeners.add(x);
  }
  
  @Override
  public void removeContainerChangeListener(ContainerChangeListener x) {
    containerChangeListeners.remove(x);
  }
  
  
  @Override
  public void fireChildAdded(IContainerModel parent, INodeModel node, Map<String, Object> context) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childAdded(parent, node, context);
    }
  }

  @Override
  public void fireChildRemoved(IContainerModel parent, INodeModel node) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childRemoved(parent, node);
    }
  }

  
  @Override
  public List<INodeModel> selectNodeModels (String expr) {
    IPathExpression pathExpr;
    try {
      pathExpr = PathParser.parse(expr);
    } catch (ParseException ex) {
      throw new IllegalArgumentException(ex);
    }
    List<INodeModel> found = new ArrayList<>();
    pathExpr.matches(this, null, new Consumer<INodeModel>() {
      @Override
      public void accept(INodeModel model) {
        found.add(model);
      }
    });
    return found;
  }

  @Override
  public List<IItemModel> selectItemModels (String expr) {
    IPathExpression pathExpr;
    try {
      pathExpr = PathParser.parse(expr);
    } catch (ParseException ex) {
      throw new IllegalArgumentException(ex);
    }
    pathExpr.dump();
    List<IItemModel> found = new ArrayList<>();
    pathExpr.matches(this, null, new Consumer<INodeModel>() {
      @Override
      public void accept(INodeModel model) {
        if (model instanceof IItemModel) {
          found.add((IItemModel)model);
        }
      }
    });
    return found;
  }

  @Override
  public INodeModel selectNodeModel (String expr) {
    List<INodeModel> found = selectNodeModels(expr);
    switch (found.size()) {
    case 0 :
      throw new IllegalArgumentException("No node models matching: " + expr);
    case 1 :
      return found.get(0);
    default :
      throw new IllegalArgumentException(found.size() + " node models matching: " + expr + ". I.e., more than one");
    }
  }


  @Override
  public IItemModel selectItemModel (String expr) {
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

}
