package org.gyfor.formref;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;


@Component(service=FormReference.class, configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class FormReference {

  @Configurable(name="entity", required=true)
  private String entityClassName;
  
  
  @Configurable(name="html")
  private String htmlFileName = null;
  
  
  @Configurable(name="actionHandler")
  private String actionHandlerClassName;
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    if (htmlFileName == null) {
      htmlFileName = entityClassName + ".html";
    }
  }

  
  public String getEntityClassName() {
    return entityClassName;
  }

  
  public String getHTMLFileName() {
    return htmlFileName;
  }
  
  
  public String getActionHandlerClassName() {
    return actionHandlerClassName;
  }
  
}
