package org.gyfor.pebble;

import org.gyfor.pebble.impl.BundleContextLoader;
import org.gyfor.pebble.impl.Template;
import org.osgi.framework.BundleContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;


public class TemplateEngine {

  private final BundleContext bundleContext;
  
  private PebbleEngine engine;
  

  public TemplateEngine (BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }
  
  
  public ITemplate getTemplate (String templateName) {
    if (engine == null) {
      // Lazily create basic template engine
      synchronized (this) {
        if (engine == null) {
          // Initialize the template engine.
          Builder builder = new PebbleEngine.Builder();

          // Add bundle specific loader
          Loader<?> loader = new BundleContextLoader(bundleContext);
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
