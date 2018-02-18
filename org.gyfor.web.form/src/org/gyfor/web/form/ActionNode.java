package org.gyfor.web.form;

import java.io.Writer;
import java.util.Map;

import org.gyfor.web.form.state.StateMachine;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;


public class ActionNode extends AbstractRenderableNode {

  private final Expression<?> actionNameExpression;
  
  private final boolean requiresValidEntry;
  
  private final MapExpression withExpression;


  public ActionNode(int lineNumber, Expression<?> actionNameExpression, boolean requiresValidEntry, MapExpression withExpression) {
    super(lineNumber);
    this.actionNameExpression = actionNameExpression;
    this.requiresValidEntry = requiresValidEntry;
    this.withExpression = withExpression;
  }

  
//  private static PebbleEngine getPebbleEngine (PebbleTemplateImpl self) {
//    PebbleEngine pebbleEngine;
//    try {
//      Class<?> selfClass = self.getClass();
//      Field engineField = selfClass.getDeclaredField("engine");
//      engineField.setAccessible(true);
//      pebbleEngine = (PebbleEngine)engineField.get(self);
//    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
//      throw new RuntimeException(ex);
//    }
//    return pebbleEngine;
//  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException {
    String actionName = (String)actionNameExpression.evaluate(self, context);
    if (actionName == null) {
      throw new PebbleException(null, "option tag: option name evaluates to null", getLineNumber(), self.getName());
    }
        
    StateMachine<?,?> stateMachine = (StateMachine<?,?>)context.getScopeChain().get("stateMachine");
    Enum<?> action;
    try {
      // Validate the option name
      action = stateMachine.valueOf(actionName);
    } catch (IllegalArgumentException ex) {
      throw new PebbleException(null, "option tag: '" + actionName + "' does not name an action in the stateMachine", getLineNumber(), self.getName());
    }
    boolean requiresValidEntry = stateMachine.requiresValidEntry(actionName);

    //    INodeModel fieldModel = ((INameMappedModel)model).getMember(fieldRef);
//    if (fieldModel == null) {
//      throw new PebbleException(null, "field tag: no member named '" + fieldRef + "' in name mapped model " + model.getCanonicalName());
//    }

    // Get any withValues
    Map<String, Object> withValues = null;
    if (withExpression != null) {
      withValues = (Map<String, Object>)withExpression.evaluate(self, context);
    }
    
    
//    ITemplateEngine templateEngine = (ITemplateEngine)context.getScopeChain().get("engine");
//    
//    ModelHtmlBuilder.buildHtml(templateEngine, writer, fieldModel, withValues);
  }


  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
