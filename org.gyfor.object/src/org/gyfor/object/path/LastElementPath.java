package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;
import org.gyfor.object.IRepeatingNode;


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
  public void matches(INode node, Trail<INode> trail, Consumer<INode> x) {
    if (node instanceof IRepeatingNode) {
      IRepeatingNode repeating = (IRepeatingNode)node;
      int n = repeating.size();
      if (n > 0) {
        INode element = repeating.getElementNode(n - 1);
        super.matches(element, new Trail<>(trail, element), x);
      }
    } else {
      throw new IllegalArgumentException("'last element' within a path expression, only applies to repeating node");
    }
  }

}
