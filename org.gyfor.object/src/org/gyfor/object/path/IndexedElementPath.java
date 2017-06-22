package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;
import org.gyfor.object.IRepeatingNode;


public class IndexedElementPath extends StepPath implements IPathExpression {

  /**
   * This index is 1 based, to conform with the xpath specification.
   */
  private int index;
  
  public IndexedElementPath (StepPath parent, int index) {
    super(parent);
    this.index = index;
  }
  
  public IndexedElementPath (StepPath parent, String s) {
    this (parent, Integer.parseInt(s));
  }

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("[" + index + "]");
    super.dump(level + 1);
  }

 
  @Override
  public void matches(INode node, Trail<INode> trail, Consumer<INode> x) {
    if (node instanceof IRepeatingNode) {
      IRepeatingNode repeating = (IRepeatingNode)node;
      int n = repeating.size();
      if (index < n) {
        INode element = repeating.getElementNode(index);
        super.matches(element, new Trail<>(trail, element), x);
      }
    } else {
      throw new IllegalArgumentException("indexed element ([]) within a path expression, only applies to repeating nodes");
    }
  }

  
}
