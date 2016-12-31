package org.gyfor.object.model.path;

import java.util.List;


public class LastElementPath extends StepPath implements IPathExpression {

  public LastElementPath (StepPath parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("[last]");
    super.dump(level + 1);
  }

  @Override
  public boolean matches(IObjectWrapper wrapper, Trail trail, IObjectVisitable x) {
    List<IObjectWrapper> children = wrapper.getChildren();
    int n = children.size();
    if (n > 0) {
      IObjectWrapper last = children.get(n - 1);
      return super.matches(last, new Trail(trail, last), x);
    } else {
      return false;
    }
  }

  @Override
  public boolean matches(IObjectWrapper wrapper, IFieldVisitable x) {
    List<IObjectWrapper> children = wrapper.getChildren();
    int n = children.size();
    if (n > 0) {
      IObjectWrapper last = children.get(n - 1);
      return super.matches(last, x);
    } else {
      return false;
    }
  }

}
