package org.gyfor.template.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;

import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;


@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class TemplateEngineFactory implements ITemplateEngineFactory {

  @Configurable
  private Path templateDir = Paths.get("templates");
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    if (!templateDir.isAbsolute()) {
      // A relative path, so make it relative to the config file
      Dictionary<String, Object> dict = componentContext.getProperties();
      String cfgName = (String)dict.get("felix.fileinstall.filename");
      if (cfgName == null) {
        // This component was not started using Felix fileinstall, so try for the default configuration location
        cfgName = System.getProperty("felix.fileinstall.dir");
        if (cfgName == null) {
          throw new RuntimeException("Relative path name with no 'felix.fileinstall.filename' or 'felix.fileinstall.dir'");
        }
      }
      if (cfgName.startsWith("file:/")) {
        cfgName = cfgName.substring(6);
      }
      Path cfgPath = Paths.get(cfgName);
      templateDir = cfgPath.resolveSibling(templateDir).normalize();
    }
  }
  
  
  @Deactivate
  public void deactivate () {
  }

  
  @Override
  public ITemplateEngine buildTemplateEngine(BundleContext defaultBundleContext) {
    return new TemplateEngine(templateDir, defaultBundleContext);
  }

  
  @Override
  public ITemplateEngine buildTemplateEngine(ComponentContext componentContext) {
    return buildTemplateEngine(componentContext.getBundleContext());
  }

}
