package org.gyfor.web.form.defn;

import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;


public abstract class AbstractTemplateFunction implements Function {

  protected final PebbleTemplate template;
  

  AbstractTemplateFunction (BundleContext bundleContext, String templateName) {
    // Initialize the template engine.
    Loader<String> loader = new BundleContextLoader(bundleContext);
    PebbleEngine templateEngine = new PebbleEngine.Builder().loader(loader).build();

    try {
      template = templateEngine.getTemplate(templateName);
    } catch (PebbleException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  @Override
  public abstract List<String> getArgumentNames();
  

  @Override
  public abstract Object execute(Map<String, Object> args);

}
