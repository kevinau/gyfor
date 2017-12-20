package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.IContainerNode;
import org.gyfor.object.INode;

public class DescendentPath<T extends INode> extends StepPath<T> implements IPathExpression<T> {

  public DescendentPath (StepPath<T> parent) {
    super(parent);
  }

  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("..");
    super.dump(level + 1);
  }

  @Override
  public void matches(T node, Trail<T> trail, Consumer<T> consumer) {
    matchDeep(node, trail, consumer);
  }
  
  
  @SuppressWarnings("unchecked")
  private boolean matchDeep(T node, Trail<T> trail, Consumer<T> consumer) {
    Trail<T> trail2 = new Trail<T>(trail, node);
    super.matches(node, trail2, consumer);
    
    if (node instanceof IContainerNode) {
      IContainerNode<T> container = (IContainerNode<T>)node;
      for (T child : container.getContainerNodes()) {
        matchDeep(child, trail, consumer);
      }
    }
    return true;
  }
  
}
