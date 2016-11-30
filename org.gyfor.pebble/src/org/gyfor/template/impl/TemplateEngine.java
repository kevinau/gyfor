package org.gyfor.template.impl;

import java.nio.file.Path;

import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.osgi.framework.BundleContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;


public class TemplateEngine implements ITemplateEngine {

  private final Path templateDir;
  private final BundleContext defaultBundleContext;
  
  private PebbleEngine engine;
  

  public TemplateEngine (Path templateDir, BundleContext defaultBundleContext) {
    this.templateDir = templateDir;
    this.defaultBundleContext = defaultBundleContext;
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
          Loader<?> loader = new MultiLoader(templateDir, defaultBundleContext);
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
