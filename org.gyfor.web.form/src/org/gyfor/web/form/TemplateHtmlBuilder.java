package org.gyfor.web.form;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.plcore.template.ITemplate;
import org.plcore.template.ITemplateEngine;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.plan.EntityLabelGroup;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.type.IType;

import com.mitchellbosecke.pebble.template.ScopeChain;

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
  
  
  public ScopeChain buildHtml (Writer writer, INodeModel nodeModel, Map<String, Object> withValues) {
    // Build template name
    IEntityPlan<?> entityPlan = nodeModel.getParentEntity().getPlan();
    String templateName = entityPlan.getClassName();
    if (!(nodeModel instanceof IEntityModel)) {
      templateName += "#" + nodeModel.getQualifiedPlanName();
    }
    String defaultName;
    if (nodeModel instanceof IItemModel) {
      IType<?> type = ((IItemPlan<?>)nodeModel.getPlan()).getType();
      defaultName = type.getClass().getSimpleName();
    } else {
      defaultName = nodeModel.getClass().getSimpleName();
    }
    templateName += "(" + defaultName + ")";
    
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
    System.out.println(">>>>>>>>>>>>>> " + nodeModel.getName() + " " + nodeModel.getEffectiveEntryMode());
    templateContext.put("mode", nodeModel.getEffectiveEntryMode().toString());
    if (withValues != null) {
      templateContext.putAll(withValues);
    }
    
    return nodeTemplate.evaluate2(writer, templateContext);
  }
  
  
  public String buildTitle (INodeModel nodeModel, Map<String, Object> withValues) {
    String title = null;
    if (withValues != null) {
      title = (String)withValues.get("title");
    }
    if (title == null) {
      IEntityPlan<?> entityPlan = nodeModel.getParentEntity().getPlan();
      EntityLabelGroup labelGroup = entityPlan.getLabels();
      title = labelGroup.getTitle();
    }
    return title;
  }
  
}
