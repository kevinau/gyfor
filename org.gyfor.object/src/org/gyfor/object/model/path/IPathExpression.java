package org.gyfor.object.model.path;

import org.gyfor.object.model.NodeModel;


public interface IPathExpression {

  public void dump(int level);

  public default void dump() {
    dump(0);
  }

  public default void indent(int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }

  public void matches(NodeModel model, Trail trail, INodeVisitable x);
    

  //public boolean matches(NodeModel model, IItemVisitable x);

}
