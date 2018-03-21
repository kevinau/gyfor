package org.plcore.template;

import org.osgi.framework.BundleContext;

public interface ITemplateEngineFactory {

  public ITemplateEngine buildTemplateEngine (BundleContext namedBundleContext);
  
}
