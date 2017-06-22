package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;


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

  public void matches(INode node, Trail<INode> trail, Consumer<INode> x);

}
