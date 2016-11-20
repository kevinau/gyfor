package org.gyfor.web.form;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.object.context.PlanFactory;
import org.gyfor.object.model.EntityModel;
import org.gyfor.object.model.ItemModel;
import org.gyfor.object.model.NodeModel;
import org.gyfor.object.model.RootModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.type.IType;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.party.Person;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * A class that builds HTML for a model.  Pebble templates--that contain HTML--are used 
 * to build the HTML.
 */
@Component
public class ModelHtmlBuilder {

  private FormTemplateEngine templateEngine;
  
  
  @Reference
  public void setFormTemplateEngine (FormTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }
  
  
  public void unsetFormTemplateEngine (FormTemplateEngine templateEngine) {
    this.templateEngine = null;
  }

  
  @Activate
  public void activate (BundleContext bundleContext) {
    RootModel rootModel = new RootModel();
    
    PlanFactory objectContext = new PlanFactory();
    IEntityPlan<Person> entityPlan = objectContext.getEntityPlan(Person.class);
    
    Person instance = new Person("Kevin Holloway", "Kevin", "0447 252 976", null, "kholloway@geckosoftware.com.au");
    EntityModel entityModel = rootModel.buildEntityModel(entityPlan, instance);
    Writer writer = new StringWriter();
    buildHtml(templateEngine.getPebbleEngine(), writer, entityModel, null);
    System.out.println(writer.toString());
  }
  
  
  public static void buildHtml (PebbleEngine templateEngine, Writer writer, NodeModel nodeModel, Map<String, Object> withValues) {
    // Build template name
    String templateName = nodeModel.getCanonicalName();
    String defaultName;
    if (nodeModel.isItem()) {
      IType<?> type = ((ItemModel)nodeModel).getPlan().getType();
      defaultName = type.getClass().getSimpleName();
    } else {
      defaultName = nodeModel.getClass().getSimpleName();
    }
    templateName += "(" + defaultName + ")";
    
    PebbleTemplate nodeTemplate;
    try {
      nodeTemplate = templateEngine.getTemplate(templateName);
    } catch (PebbleException ex) {
      throw new RuntimeException(ex);
    }
    
    Map<String, Object> templateContext = new HashMap<>();
    templateContext.put("model", nodeModel);

    // The labels are loaded individually.  This allows them to be overridden by field "with" values.
    INodePlan nodePlan = nodeModel.getPlan();
    nodePlan.getLabels().loadAll(templateContext);

    // The following are a convenience to template writers
    templateContext.put("id", nodeModel.getId());
    templateContext.put("plan", nodePlan);
    if (nodeModel.isItem()) {
      templateContext.put("type", ((IItemPlan<?>)nodePlan).getType());
    }
    if (withValues != null) {
      templateContext.putAll(withValues);
    }
    
    try {
      nodeTemplate.evaluate(writer, templateContext);
    } catch (IOException | PebbleException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
