package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;
import org.gyfor.object.IRepeatingNode;


public class LastElementPath<T extends INode> extends StepPath<T> implements IPathExpression<T> {

  public LastElementPath (StepPath<T> parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("[last]");
    super.dump(level + 1);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void matches(T node, Trail<T> trail, Consumer<T> x) {
    if (node instanceof IRepeatingNode) {
      IRepeatingNode<T> repeating = (IRepeatingNode<T>)node;
      int n = repeating.size();
      if (n > 0) {
        T element = repeating.getIndexedNode(n - 1);
        super.matches(element, new Trail<>(trail, element), x);
      }
    } else {
      throw new IllegalArgumentException("'last element' within a path expression, only applies to repeating node");
    }
  }

}
