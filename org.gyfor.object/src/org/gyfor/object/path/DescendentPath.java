package org.gyfor.object.path;

import java.util.Iterator;
import java.util.function.Consumer;

import org.gyfor.object.IContainerNode;
import org.gyfor.object.INode;

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
  public void matches(INode node, Trail<INode> trail, Consumer<INode> x) {
    matchDeep(node, trail, x);
  }
  
  
  private boolean matchDeep(INode node, Trail<INode> trail, Consumer<INode> x) {
    Trail<INode> trail2 = new Trail<>(trail, node);
    super.matches(node, trail2, x);
    
    if (node instanceof IContainerNode) {
      IContainerNode container = (IContainerNode)node;
      Iterator<INode> i = container.getChildNodes();
      while (i.hasNext()) {
        INode child = i.next();
        matchDeep(child, trail, x);
      }
    }
    return true;
  }
  
}
