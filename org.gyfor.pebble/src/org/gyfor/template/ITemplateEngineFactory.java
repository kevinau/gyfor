package org.gyfor.template;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public interface ITemplateEngineFactory {

  public ITemplateEngine buildTemplateEngine (BundleContext primaryBundleContext);
  
  public ITemplateEngine buildTemplateEngine (ComponentContext componentContext);
  
}
