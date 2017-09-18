package org.gyfor.doc;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;


@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DocumentType implements IDocumentType {

  @Configurable(required = true)
  private String name;
  
  @Configurable(name = "class", required = true)
  private Class<?> klass;
  
  @Configurable(required = true)
  private String description;
  
  
  @Activate
  public void activate(ComponentContext context) {
    ComponentConfiguration.load(this, context);
  }
  
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public Class<?> getDataClass() {
    return klass;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
