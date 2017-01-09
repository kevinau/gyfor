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


@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class TemplateEngineFactory implements ITemplateEngineFactory {

  @Configurable(required = true)
  private Path templateDir;
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);
    
    if (!templateDir.isAbsolute()) {
      // A relative path, so make it relative to the config file
      Dictionary<String, Object> dict = componentContext.getProperties();
      String cfgName = (String)dict.get("felix.fileinstall.filename");
      if (cfgName == null) {
        throw new RuntimeException("Relative path name with no 'felix.fileinstall.filename'");
      }
      if (!cfgName.startsWith("file:/")) {
        throw new RuntimeException("'felix.fileinstall.filename' does not name a file system file");
      }
      Path cfgPath = Paths.get(cfgName.substring(6));
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
