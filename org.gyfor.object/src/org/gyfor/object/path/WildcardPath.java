package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.IContainerNode;
import org.gyfor.object.INode;


public class WildcardPath<T extends INode> extends StepPath<T> implements IPathExpression<T> {

  public WildcardPath (StepPath<T> parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("*  (wildcard path)");
    super.dump(level + 1);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void matches(T node, Trail<T> trail, Consumer<T> consumer) {
    if (node instanceof IContainerNode) {
      IContainerNode<T> container = (IContainerNode<T>)node;
      for (T child : container.getContainerNodes()) {
        super.matches(child, new Trail<>(trail, child), consumer);
      }
    } else {
      consumer.accept(node);
    }
  }

}
