package org.gyfor.object.model.path;

import org.gyfor.object.model.NodeModel;

public class DescendentPath extends StepPath implements IPathExpression {

  public DescendentPath (StepPath parent) {
    super(parent);
  }

  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("..");
    super.dump(level + 1);
  }

  @Override
  public void matches(NodeModel model, Trail trail, INodeVisitable x) {
    matchDeep(model, trail, x);
  }
  
  
  private boolean matchDeep(NodeModel model, Trail trail, INodeVisitable x) {
    Trail trail2 = new Trail(trail, model);
    super.matches(model, trail2, x);
    
    for (IObjectWrapper child : wrapper.getChildren()) {
      matchDeep(child, x);
    }
    return true;
  }

//  private boolean matchDeep(IObjectWrapper wrapper, Trail trail, IObjectVisitable x) {
//    Trail trail2 = new Trail(trail, wrapper);
//    super.matches(wrapper, trail2, x);
//    
//    for (IObjectWrapper child : wrapper.getChildren()) {
//      matchDeep(child, trail2, x);
//    }
//    return true;
//  }
  
//  @Override
//  public boolean matches(IObjectWrapper wrapper, IFieldVisitable x) {
//    matchDeep(wrapper, x);
//    return true;
//  }
  
  
}
