package org.gyfor.web.form;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;


@Component (service = FormTemplateEngine.class)
public class FormTemplateEngine {

  private Logger logger = LoggerFactory.getLogger(FormTemplateEngine.class);
  
  private BundleContext defaultContext;
  
  private BundleContext globalContext;
  
  private volatile PebbleEngine pebbleEngine= null;
  

  @Activate
  public void activate (BundleContext bundleContext) {
    this.defaultContext = bundleContext;
  }
  

  @Reference (cardinality = ReferenceCardinality.OPTIONAL)
  public void setGlobalTemplateLocation (IGlobalTemplateLocation globalTemplateLocation) {
    this.globalContext = globalTemplateLocation.getBundleContext();
  }
  
  
  public void unsetGlobalTemplateLocation (IGlobalTemplateLocation globalTemplateLocation) {
    this.globalContext = null;
  }
  
  
  private void buildPebbleEngine () {
    // Initialize the template engine.
    Builder builder = new PebbleEngine.Builder();

    // Add bundle specific loader
    Loader<?> loader = new MultiLoader(defaultContext, globalContext);
    builder.loader(loader);

    // Field and other tags
    Extension extension = new AbstractExtension() {
      @Override
      public List<TokenParser> getTokenParsers() {
        List<TokenParser> parsers = new ArrayList<>();
        parsers.add(new FieldTokenParser());
        return parsers;
      }
    };
    builder.extension(extension);
    
    // Build the Pebble engine
    pebbleEngine = builder.build();
  }

  
  public Template getTemplate (String templateName) {
    // Lazily build template engine
    if (pebbleEngine == null) {
      synchronized (this) {
        if (pebbleEngine == null) {
          buildPebbleEngine();
        }
      }
    }

    Template template;
    logger.info("Get form template: " + templateName);
    try {
      template = new Template(pebbleEngine.getTemplate(templateName));
    } catch (PebbleException ex) {
      throw new RuntimeException(ex);
    }

    return template;
  }
  
  
  public PebbleEngine getPebbleEngine () {
    // Lazily build template engine
    if (pebbleEngine == null) {
      synchronized (this) {
        if (pebbleEngine == null) {
          buildPebbleEngine();
        }
      }
    }
    return pebbleEngine;
  }
  
//  public Loader<?> getLoader () {
//    // Lazily build template engine
//    if (pebbleEngine == null) {
//      buildPebbleEngine();
//    }
//    return pebbleEngine.getLoader();
//  }

}
