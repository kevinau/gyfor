package org.gyfor.web.form.defn;

import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Map;

import org.gyfor.web.form.html.SplitTemplate;
import org.pennyledger.object.EntityPlanFactory;
import org.pennyledger.object.plan.IEntityPlan;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;


public class Entity2Node extends AbstractRenderableNode {

  private static final String NL = System.getProperty("line.separator");
  
  static String ENTITY_PLAN_NAME = "ENTITY";
  
  private final Loader<?> loader;
  
  private String className;
  private MapExpression withValues;
  private BodyNode body;
  
  
  public Entity2Node(int lineNumber, String className, MapExpression withValues, BodyNode body, Loader<?> loader) {
    super(lineNumber);
    this.className = className;
    this.withValues = withValues;
    this.body = body;
    this.loader = loader;
  }


  // The following code is a workaround to get the PebbleEngine out of PebbleTemplateImpl.
  // The PebbleEngine field in PebbleTemplateImpl is private with not get method.
  private final Field engineField;
  
  {
    try {
      engineField = PebbleTemplateImpl.class.getDeclaredField("engine");
    } catch (NoSuchFieldException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  private PebbleEngine getEngine(PebbleTemplateImpl template) {
    try {
      engineField.setAccessible(true);
      return (PebbleEngine)engineField.get(template);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException {
    Class<?> klass;
    try {
      klass = Class.forName(className);
    } catch (ClassNotFoundException ex) {
      throw new PebbleException(ex, null);
    }
    IEntityPlan<?> entityPlan = EntityPlanFactory.getEntityPlan(klass);
    //context.getScopeChain().put(ENTITY_PLAN_NAME, entityPlan);

    PebbleEngine engine = getEngine(self);
    SplitTemplate htmlTemplate = splitTemplateFactory.get("entity");

    if (withValues != null) {
      ScopeChain scopeChain = context.getScopeChain();
       Map<String, Object> map = (Map<String, Object>)withValues.evaluate(self, context);
       context.getScopeChain().pushScope(map);
    }
    htmlTemplate.evaluateBefore(writer, context);
    body.render(self, writer, context);
    htmlTemplate.evaluateAfter(writer, context);
    if (withValues != null) {
      ScopeChain scopeChain = context.getScopeChain();
      scopeChain.popScope();
    }
    
//    try {
//      writer.append("<section>");
//      writer.append(NL);
//      if (legendExpr != null) {
//        String legend = (String)legendExpr.evaluate(self, context);
//        writer.append("<legend>");
//        writer.append(legend);
//        writer.append("</legend>");
//        writer.append(NL);
//      };
//      if (descriptionExpr != null) {
//        String description = (String)descriptionExpr.evaluate(self, context);
//        writer.append("<p>");
//        writer.append(description);
//        writer.append("</p>");
//        writer.append(NL);
//      };
//      writer.append("<div id='node-0'>");
//      writer.append(className);
//      writer.append(NL);
//      writer.append("</div>");
//      writer.append(NL);
//      writer.append("</section>");
//      writer.append(NL);
//    } catch (IOException ex) {
//      throw new PebbleException(ex, null);
//    }
  }


  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
