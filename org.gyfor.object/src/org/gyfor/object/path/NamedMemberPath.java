package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INameMappedNode;
import org.gyfor.object.INode;


public class NamedMemberPath<T extends INode> extends StepPath<T> implements IPathExpression<T> {

  private final String name;
  
  public NamedMemberPath (StepPath<T> parent, String name) {
    super(parent);
    this.name = name;
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println(name + "  (named member)");
    super.dump(level + 1);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void matches(T node, Trail<T> trail, Consumer<T> x) {
    if (node instanceof INameMappedNode) {
      INameMappedNode<T> mapped = (INameMappedNode<T>)node;
      T member = mapped.getNameMappedNode(name);
      if (member == null) {
        // Do nothing
      } else {
        super.matches(member, new Trail<>(trail, member), x);
      }
    } else {
      // Do nothing
    }
  }

}
