package org.gyfor.object.path;

import java.util.function.Consumer;

import org.gyfor.object.INameMappedNode;
import org.gyfor.object.INode;


public class NamedMemberPath extends StepPath implements IPathExpression {

  private final String name;
  
  public NamedMemberPath (StepPath parent, String name) {
    super(parent);
    this.name = name;
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println(name + "  (named member)");
    super.dump(level + 1);
  }

  @Override
  public void matches(INode node, Trail<INode> trail, Consumer<INode> x) {
    if (node instanceof INameMappedNode) {
      INameMappedNode mapped = (INameMappedNode)node;
      INode member = mapped.getChildNode(name);
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
