package org.gyfor.web.form;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.type.IType;
import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;

/**
 * A class that builds HTML for a model.  Pebble templates--that contain HTML--are used 
 * to build the HTML.
 */
public class TemplateHtmlBuilder {

  private final ITemplateEngine templateEngine;
  
  
  public TemplateHtmlBuilder (ITemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }
  

//  @Activate
//  public void activate (BundleContext bundleContext) {
//    RootModel rootModel = new RootModel();
//    
//    PlanContext objectContext = new PlanContext();
//    IEntityPlan<Person> entityPlan = objectContext.getEntityPlan(Person.class);
//    
//    Person instance = new Person("Kevin Holloway", "Kevin", "0447 252 976", null, "kholloway@geckosoftware.com.au");
//    EntityModel2 entityModel = rootModel.buildEntityModel(entityPlan, instance);
//    Writer writer = new StringWriter();
//    buildHtml(templateEngine.getPebbleEngine(), writer, entityModel, null);
//    System.out.println(writer.toString());
//  }
  
  
  public void buildHtml (Writer writer, INodeModel nodeModel, Map<String, Object> withValues) {
    // Build template name
    System.out.println("................... " + nodeModel.getClass());
    System.out.println("................... " + nodeModel);
    IEntityPlan<?> entityPlan = nodeModel.getParentEntity().getPlan();
    String templateName = entityPlan.getClassName();
    if (!(nodeModel instanceof IEntityModel)) {
      templateName += "#" + nodeModel.getQualifiedPlanName();
    }
    System.out.println("................... " + templateName);
    String defaultName;
    if (nodeModel instanceof IItemModel) {
      IType<?> type = ((IItemPlan<?>)nodeModel.getPlan()).getType();
      defaultName = type.getClass().getSimpleName();
    } else {
      defaultName = nodeModel.getClass().getSimpleName();
    }
    System.out.println("................... " + defaultName);
    templateName += "(" + defaultName + ")";
    System.out.println("................... " + templateName);
    
    ITemplate nodeTemplate = templateEngine.getTemplate(templateName);
    
    Map<String, Object> templateContext = new HashMap<>();
    templateContext.put("model", nodeModel);

    // The labels are loaded individually.  This allows them to be overridden by field "with" values.
    INodePlan nodePlan = nodeModel.getPlan();
    nodePlan.getLabels().extractAll(templateContext);

    // The following are a convenience to template writers
    int parentId = 0;
    if (nodeModel.getParent() != null) {
      parentId = nodeModel.getParent().getNodeId();
    }
    templateContext.put("parentId", parentId);
    
    templateContext.put("id", nodeModel.getNodeId());
    templateContext.put("plan", nodePlan);
    if (nodeModel instanceof IItemModel) {
      IType<?> type = ((IItemPlan<?>)nodeModel.getPlan()).getType();
      templateContext.put("type", type);
    }
    if (withValues != null) {
      templateContext.putAll(withValues);
    }
    
    nodeTemplate.evaluate(writer, templateContext);
  }
  
  
  public String buildHtml(INodeModel nodeModel, Map<String, Object> withValues) {
    StringWriter writer = new StringWriter();
    buildHtml(writer, nodeModel, withValues);
    return writer.toString();
  }

}