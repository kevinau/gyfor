package org.gyfor.web.form.defn;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.pennyledger.object.label.EntityLabels;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;


public class EntityHTMLBuilder {

  private BundleContextLoader loader;
  private PebbleEngine pebbleEngine;
  private PebbleTemplate defaultEntityTemplate;
  
  private Map<String, PebbleTemplate> templateCache = new HashMap<>();
  
  
  public EntityHTMLBuilder (BundleContext bundleContext, String templateDir) {
    loader = new BundleContextLoader(bundleContext, templateDir);
  }


  private void buildPebbleEngine () {
    // Initialize the template engine.
    Builder builder = new PebbleEngine.Builder();

    // Add bundle specific loader
    builder.loader(loader);

    // Add page extensions
    Extension pageExtension = new AbstractExtension() {
      @Override
      public List<TokenParser> getTokenParsers() {
        List<TokenParser> parsers = new ArrayList<>();
        parsers.add(new Entity2TokenParser(loader));
        return parsers;
      }
    };
    builder.extension(pageExtension);
    
    // Build the Pebble engine
    pebbleEngine = builder.build();
  }
  
  

  private PebbleTemplate getEntityTemplate (String templateName) {
    PebbleTemplate template;
    try {
      if (pebbleEngine == null) {
        buildPebbleEngine();
      }
      template = pebbleEngine.getTemplate(templateName);
    } catch (PebbleException ex) {
      throw new RuntimeException(ex);
    }
    return template;
  }
  
  
  private PebbleTemplate getDefaultEntityTemplate() {
    if (defaultEntityTemplate == null) {
      defaultEntityTemplate = getEntityTemplate("defaultEntity");
    }
    return defaultEntityTemplate;
  }


  public void buildHtml (Writer writer, String entityName, int id) {
    // Can we use an existing template?
    PebbleTemplate template = templateCache.get(entityName);
    if (template != null) {
      // Yes. We're good to go.
    } else {
      // No. So is there a custom template for this entity?
      String templateName = entityName;
      if (loader.exists(templateName)) {
        // A custom template exists, so compile it and add it to the cache.
        template = getEntityTemplate(templateName);
        templateCache.put(entityName, template);
      } else {
        // Otherwise, use the standard standardEntity template.
        template = getDefaultEntityTemplate();
      }

      EntityLabels labelGroup = EntityLabels.extract(entityName);
      Map<String, Object> entityContext = new HashMap<>();
      entityContext.put("id", id);
      entityContext.put("shortTitle", labelGroup.getShortTitle());
      entityContext.put("title", labelGroup.getTitle());
      entityContext.put("description", labelGroup.getDescription());
      try {
        template.evaluate(writer, entityContext);
      } catch (PebbleException | IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

}
