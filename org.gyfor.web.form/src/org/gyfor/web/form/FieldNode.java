package org.gyfor.web.form;

import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Map;

import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;


public class FieldNode extends AbstractRenderableNode {

  private final Expression<?> fieldNameExpression;

  private final MapExpression withExpression;


  public FieldNode(int lineNumber, Expression<?> fieldNameExpression, MapExpression withExpression) {
    super(lineNumber);
    this.fieldNameExpression = fieldNameExpression;
    this.withExpression = withExpression;
  }

  
  private static PebbleEngine getPebbleEngine (PebbleTemplateImpl self) {
    PebbleEngine pebbleEngine;
    try {
      Class<?> selfClass = self.getClass();
      Field engineField = selfClass.getDeclaredField("engine");
      engineField.setAccessible(true);
      pebbleEngine = (PebbleEngine)engineField.get(self);
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    return pebbleEngine;
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException {
    String fieldRef = (String)fieldNameExpression.evaluate(self, context);
    if (fieldRef == null) {
      throw new PebbleException(null, "field tag: field reference evaluates to null", getLineNumber(), self.getName());
    }
    
    INodeModel model = (INodeModel)context.getScopeChain().get("model");
    if (!model.isNameMapped()) {
      throw new PebbleException(null, "field tag: no 'name mapped' model in scope", getLineNumber(), self.getName());
    }
    
    INodeModel fieldModel = ((INameMappedModel)model).getMember(fieldRef);
    if (fieldModel == null) {
      throw new PebbleException(null, "field tag: no member named '" + fieldRef + "' in name mapped model " + model.getCanonicalName());
    }

    // Get any withValues
    Map<String, Object> withValues = null;
    if (withExpression != null) {
      withValues = (Map<String, Object>)withExpression.evaluate(self, context);
    }

    // The following nastiness is because PebbleTemplateImpl does not expose its template engine
    PebbleEngine pebbleEngine = getPebbleEngine(self);
    
    ModelHtmlBuilder.buildHtml(pebbleEngine, writer, fieldModel, withValues);
  }


  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
