package org.gyfor.pebble.impl;

import java.util.Enumeration;
import java.util.Properties;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.pebble.ITemplateEngine;
import org.gyfor.pebble.ITemplateEngineFactory;
import org.gyfor.value.ExistingDirectory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;


@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class TemplateEngineFactory implements ITemplateEngineFactory {

  @Configurable(required = true)
  private ExistingDirectory webProjectPath;
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    Properties sysp = System.getProperties();
    for (Enumeration<?> e = sysp.propertyNames(); e.hasMoreElements(); ) {
      Object key = e.nextElement();
      Object value = sysp.get(key);
      //System.out.println("............" + key + " = " + value);
    }
    //System.out.println("....................................");
  }
  
  
  @Deactivate
  public void deactivate () {
  }

  
  @Override
  public ITemplateEngine buildTemplateEngine(BundleContext primaryBundleContext) {
    return new TemplateEngine(primaryBundleContext, webProjectPath);
  }

}
