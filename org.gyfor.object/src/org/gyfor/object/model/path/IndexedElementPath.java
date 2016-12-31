package org.gyfor.object.model.path;

import java.util.List;

import org.gyfor.object.model.NodeModel;
import org.gyfor.object.model.RepeatingModel;


public class IndexedElementPath extends StepPath implements IPathExpression {

  /**
   * This index is 1 based, to conform with the xpath specification.
   */
  private int index;
  
  public IndexedElementPath (StepPath parent, int index) {
    super(parent);
    this.index = index;
  }
  
  public IndexedElementPath (StepPath parent, String s) {
    this (parent, Integer.parseInt(s));
  }

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("[" + index + "]");
    super.dump(level + 1);
  }

  @Override
  public void matches(NodeModel model, Trail trail, INodeVisitable x) {
    if (model instanceof RepeatingModel) {
      RepeatingModel repeating = (RepeatingModel)model;
      NodeModel member = repeating.etMember(name);
    List<IObjectWrapper> children = wrapper.getChildren();
    int n = children.size();
    if (n > 0 && (index - 1) < n) {
      IObjectWrapper elem = children.get(index - 1);
      return super.matches(elem, new Trail(trail, elem), x);
    } else {
      return false;
    }
  }
  
  @Override
  public boolean matches(IObjectWrapper wrapper, IFieldVisitable x) {
    List<IObjectWrapper> children = wrapper.getChildren();
    int n = children.size();
    if (n > 0 && (index - 1) < n) {
      IObjectWrapper elem = children.get(index - 1);
      return super.matches(elem, x);
    } else {
      return false;
    }
  }
  
}
