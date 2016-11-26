package org.gyfor.pebble.impl;

import org.gyfor.pebble.ITemplate;
import org.gyfor.pebble.ITemplateEngine;
import org.osgi.framework.BundleContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;


public class TemplateEngine implements ITemplateEngine {

  private final BundleContext primaryBundleContext;
  private final BundleContext globalBundleContext;
  
  private PebbleEngine engine;
  

  public TemplateEngine (BundleContext primaryBundleContext, BundleContext globalBundleContext) {
    this.primaryBundleContext = primaryBundleContext;
    this.globalBundleContext = globalBundleContext;
  }
  
  
  /* (non-Javadoc)
   * @see org.gyfor.pebble.ITemplateEngine#getTemplate(java.lang.String)
   */
  @Override
  public ITemplate getTemplate (String templateName) {
    if (engine == null) {
      // Lazily create basic template engine
      synchronized (this) {
        if (engine == null) {
          // Initialize the template engine.
          Builder builder = new PebbleEngine.Builder();

          // Add bundle specific loader
          Loader<?> loader = new DualBundleContextLoader(primaryBundleContext, globalBundleContext);
          builder.loader(loader);

          // Build the Pebble engine
          engine = builder.build();
        }
      }
    }
    PebbleTemplate p;
    try {
      p = engine.getTemplate(templateName);
    } catch (PebbleException ex) {
      throw new RuntimeException(ex);
    }
    return new Template(p);
  }

}
