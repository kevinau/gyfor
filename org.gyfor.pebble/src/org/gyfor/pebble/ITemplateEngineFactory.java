package org.gyfor.pebble;

import org.osgi.framework.BundleContext;

public interface ITemplateEngineFactory {

  public ITemplateEngine buildTemplateEngine (BundleContext primaryBundleContext);
  
}
