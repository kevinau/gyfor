package org.plcore.template.impl;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.plcore.template.IDefaultTemplateLoader;
import org.plcore.template.ITemplateEngine;
import org.plcore.template.ITemplateEngineFactory;


@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class TemplateEngineFactory implements ITemplateEngineFactory {

  @Reference
  private IDefaultTemplateLoader defaultTemplateLoader;
  
  @Activate
  public void activate () {
  }
//    ComponentConfiguration.load(this, componentContext);
//    
//    if (!templateDir.isAbsolute()) {
//      // A relative path, so make it relative to the config file
//      Dictionary<String, Object> dict = componentContext.getProperties();
//      String cfgName = (String)dict.get("felix.fileinstall.filename");
//      if (cfgName == null) {
//        // This component was not started using Felix fileinstall, so try for the default configuration location
//        cfgName = System.getProperty("felix.fileinstall.dir");
//        if (cfgName == null) {
//          throw new RuntimeException("Relative path name with no 'felix.fileinstall.filename' or 'felix.fileinstall.dir'");
//        }
//      }
//      if (cfgName.startsWith("file:/")) {
//        cfgName = cfgName.substring(6);
//      }
//      Path cfgPath = Paths.get(cfgName);
//      templateDir = cfgPath.resolveSibling(templateDir).normalize();
//    }
//  }
  
  
//  @Deactivate
//  public void deactivate () {
//  }

  
  @Override
  public ITemplateEngine buildTemplateEngine(BundleContext namedBundleContext) {
    return new TemplateEngine(namedBundleContext, defaultTemplateLoader);
  }

}
