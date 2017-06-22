package org.gyfor.object.path;

import java.util.Iterator;
import java.util.function.Consumer;

import org.gyfor.object.IContainerNode;
import org.gyfor.object.INode;


public class WildcardPath extends StepPath implements IPathExpression {

  public WildcardPath (StepPath parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("*  (wildcard path)");
    super.dump(level + 1);
  }

  @Override
  public void matches(INode node, Trail<INode> trail, Consumer<INode> x) {
    if (node instanceof IContainerNode) {
      IContainerNode container = (IContainerNode)node;
      Iterator<INode> i = container.getChildNodes();
      while (i.hasNext()) {
        INode child = i.next();
        super.matches(child, new Trail<>(trail, child), x);
      }
    } else {
      x.accept(node);
    }
  }

}
