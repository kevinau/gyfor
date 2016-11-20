package org.gyfor.web.form.defn;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;


public class ComponentNode extends AbstractRenderableNode {

  private final String templateName;
  private final MapExpression mapExpression;


  public ComponentNode(int lineNumber, String templateName, MapExpression mapExpression) {
    super(lineNumber);
    this.templateName = templateName;
    this.mapExpression = mapExpression;
  }


  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context)
      throws PebbleException, IOException {
    if (templateName != null) {
      Map<?, ?> map = Collections.emptyMap();
      if (this.mapExpression != null) {
        map = this.mapExpression.evaluate(self, context);
      }
      self.includeTemplate(writer, context, templateName, map);
    }
  }


  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
