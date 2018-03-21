package org.gyfor.formref;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;


@Component(service=FormReference.class, configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class FormReference {

  @Configurable(name="entity", required=true)
  private String entityClassName;
  
  
  @Configurable(name="html")
  private String htmlFileName = null;
  
  
  @Configurable(name="stateMachine")
  private String stateMachineFactoryClassName;
  
  
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
  
  
  public String getStateMachineFactoryClassName() {
    return stateMachineFactoryClassName;
  }
  
}
