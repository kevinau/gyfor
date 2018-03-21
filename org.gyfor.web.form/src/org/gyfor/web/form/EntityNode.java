package org.gyfor.web.form;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.ILabelGroup;

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

  private final MapExpression withExpression;
  

  public EntityNode(int lineNumber, Expression<?> entityNameExpression, BodyNode inlineBody, MapExpression withExpression) {
    super(lineNumber);
    this.entityNameExpression = entityNameExpression;
    this.inlineBody = inlineBody;
    this.withExpression = withExpression;
  }


  @SuppressWarnings("unchecked")
  @Override
  public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException, IOException {
    String entityName;
    if (entityNameExpression == null) {
      entityName = (String)context.getScopeChain().get("entityName");
      if (entityName == null) {
        throw new PebbleException(null, "no 'entityName' in context", getLineNumber(), self.getName());
      }
    } else {
      entityName = (String)entityNameExpression.evaluate(self, context);
      if (entityName == null) {
        throw new PebbleException(null, "entity tag: entity name evaluates to null", getLineNumber(), self.getName());
      }
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
    
    IEntityModel model = (IEntityModel)context.getScopeChain().get(entityName);
    if (model == null) {
      throw new PebbleException(null, "No model for entity '" + entityName + "'", getLineNumber(), self.getName());
    }
    
    Map<String, Object> addnlContext = new HashMap<>();
    IEntityPlan<?> plan = model.getPlan();
    addnlContext.put("plan", plan);

    ILabelGroup labels = plan.getLabels();
    labels.extractAll(addnlContext);
    
    Map<String, Object> withValues = null;
    if (withExpression != null) {
      withValues = (Map<String, Object>)withExpression.evaluate(self, context);
      addnlContext.putAll(withValues);
    }
    
    // Save field name and withValues in the template context as a "projection" node.  This
    // can be retrieved by application code.
    ScopeChain scopeChain = context.getScopeChain();
    ProjectionNode parentProjectionNode = (ProjectionNode)scopeChain.get("projection");
    ProjectionNode entityProjectionNode = new ProjectionNode(entityName, withValues);
    parentProjectionNode.add(entityProjectionNode);
    addnlContext.put("projection", entityProjectionNode);
    
    if (inlineBody != null) {
      // The entity template is 'inline'
      scopeChain.pushScope(addnlContext);
      inlineBody.render(self, writer, context);
      scopeChain.popScope();
    } else {
      // The entity template is included
      self.includeTemplate(writer, context, entityName + "(entity)", addnlContext);
    }
  }
  

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

}
