package org.gyfor.web.form.defn;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.web.form.FieldTokenParser;
import org.osgi.framework.BundleContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;


public class SimpleTemplate implements TemplateLocation {

  private PebbleTemplate compiledTemplate = null;
  
  private final String webContext;
  
  private final BundleContext bundleContext;
  
  private final String templateName;
  
  
  public SimpleTemplate (String webContext, BundleContext bundleContext, String templateName) {
    this.webContext = webContext;
    this.bundleContext = bundleContext;
    this.templateName = templateName;
    // Defer loading of the template until needed
  }
  
  
  private void loadTemplate () {
    // Initialize the template engine.
    Builder builder = new PebbleEngine.Builder();

    // Add bundle specific loader
    Loader<String> loader = new BundleContextLoader(bundleContext, TemplateLocation.templateDir);
    builder.loader(loader);

    // Add page extensions
    Extension pageExtension = new AbstractExtension() {
      @Override
      public List<TokenParser> getTokenParsers() {
        List<TokenParser> parsers = new ArrayList<>();
        parsers.add(new FieldTokenParser());
        return parsers;
      }
      
      @Override
      public Map<String, Object> getGlobalVariables() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("context", webContext);
        return vars;
      }
    };
    builder.extension(pageExtension);
    
    // Build the Pebble engine
    PebbleEngine engine = builder.build();

    // And compile the one and only template.
    try {
      compiledTemplate = engine.getTemplate(templateName);
    } catch (PebbleException ex) {
      throw new RuntimeException(ex);
    }
  }


  public String evaluate () {
    Writer writer = new StringWriter();
    evaluate (writer);
    return writer.toString();
  }

  
  public void evaluate (Writer writer) {
    if (compiledTemplate == null) {
      // Lazily load the template
      loadTemplate();
    }
    
    Map<String, Object> context = new HashMap<>();
    try {
      compiledTemplate.evaluate(writer, context);
    } catch (PebbleException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
