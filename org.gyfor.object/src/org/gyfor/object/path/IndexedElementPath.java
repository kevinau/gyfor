package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INode;
import org.gyfor.object.IRepeatingNode;


public class IndexedElementPath<T extends INode> extends StepPath<T> implements IPathExpression<T> {

  /**
   * This index is 1 based, to conform with the xpath specification.
   */
  private int index;
  
  public IndexedElementPath (StepPath<T> parent, int index) {
    super(parent);
    this.index = index;
  }
  
  public IndexedElementPath (StepPath<T> parent, String s) {
    this (parent, Integer.parseInt(s));
  }

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("[" + index + "]");
    super.dump(level + 1);
  }

 
  @SuppressWarnings("unchecked")
  @Override
  public void matches(T node, Trail<T> trail, Consumer<T> x) {
    if (node instanceof IRepeatingNode) {
      IRepeatingNode<T> repeating = (IRepeatingNode<T>)node;
      int n = repeating.size();
      if (index < n) {
        T element = repeating.getIndexedNode(index);
        super.matches(element, new Trail<>(trail, element), x);
      }
    } else {
      throw new IllegalArgumentException("indexed element ([]) within a path expression, only applies to repeating nodes");
    }
  }

  
}
