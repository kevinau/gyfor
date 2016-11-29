package org.gyfor.pebble.impl;

import java.nio.file.Path;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.pebble.ITemplateEngine;
import org.gyfor.pebble.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;


@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class TemplateEngineFactory implements ITemplateEngineFactory {

  @Configurable(required = true)
  private Path webProjectPath;
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    System.out.println("====================== " + webProjectPath);
  }
  
  
  @Deactivate
  public void deactivate () {
  }

  
  @Override
  public ITemplateEngine buildTemplateEngine(BundleContext primaryBundleContext) {
    return new TemplateEngine(primaryBundleContext, webProjectPath);
  }

}
