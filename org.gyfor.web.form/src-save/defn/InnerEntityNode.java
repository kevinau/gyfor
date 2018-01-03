package org.gyfor.web.form.defn;

import java.io.Writer;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;


public class InnerEntityNode extends AbstractRenderableNode {

  public InnerEntityNode(int lineNumber) {
    super(lineNumber);
  }


  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException {
    context.getScopeChain().put(Entity2Node.ENTITY_PLAN_NAME, null);
  }


  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
