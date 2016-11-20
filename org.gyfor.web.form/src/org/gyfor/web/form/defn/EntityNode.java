package org.gyfor.web.form.defn;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.object.model.EntityModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.web.form.MultiLoader;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.MapExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;


public class EntityNode extends AbstractRenderableNode {

  private final Expression<?> entityNameExpression;

  private final BodyNode inlineBody;

  private final MapExpression mapExpression;


  public EntityNode(int lineNumber, Expression<?> entityNameExpression, BodyNode inlineBody, MapExpression mapExpression) {
    super(lineNumber);
    this.entityNameExpression = entityNameExpression;
    this.inlineBody = inlineBody;
    this.mapExpression = mapExpression;
  }


  @SuppressWarnings("unchecked")
  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
    String entityName = (String)entityNameExpression.evaluate(self, context);
    if (entityName == null) {
      throw new PebbleException(null, "entity tag: entity name evaluates to null", getLineNumber(), self.getName());
    }
    
//    EntityModel<?> model = (EntityModel<?>)context.getScopeChain().get(entityName);
//    IEntityPlan plan = model.getPlan();
//    LabelGroup labels = model.getLabels();
//    
//    Class<?> klass;
//    try {
//      klass = Class.forName(className);
//    } catch (ClassNotFoundException ex) {
//      throw new PebbleException(ex, null);
//    }
//    IEntityPlan<?> entityPlan = EntityPlanFactory.getEntityPlan(klass);
//    EntityLabels entityLabels = EntityLabels.getLabels(klass);
    
    EntityModel model = (EntityModel)context.getScopeChain().get(entityName);
    if (model == null) {
      throw new PebbleException(null, "No model for entity '" + entityName + "'", getLineNumber(), self.getName());
    }
    IEntityPlan<?> plan = model.getPlan();
    ILabelGroup labels = plan.getLabels();
    
    Map<String, Object> args = new HashMap<>();
    args.put("plan", plan);
    args.put("labels", labels);
    
    if (mapExpression != null) {
      Map<String, Object> map = (Map<String, Object>)mapExpression.evaluate(self, context);
      args.putAll(map);
    }
    
    ScopeChain scopeChain = context.getScopeChain();
    if (inlineBody != null) {
      // The entity template is 'inline
      scopeChain.pushScope(args);
      inlineBody.render(self, writer, context);
      scopeChain.popScope();
    } else {
      // The entity template is included
      MultiLoader loader = (MultiLoader)scopeChain.get("loader");
      if (loader.existsAsFQCN(entityName)) {
        self.includeTemplate(writer, context, entityName, args);
      } else {
        self.includeTemplate(writer, context, "entity", args);
      }
    }
  }
  

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
