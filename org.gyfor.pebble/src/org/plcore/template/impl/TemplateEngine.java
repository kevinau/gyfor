package org.plcore.template.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.plcore.template.IDefaultTemplateLoader;
import org.plcore.template.ITemplate;
import org.plcore.template.ITemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.PebbleEngine.Builder;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;


public class TemplateEngine implements ITemplateEngine {

//  private final Path templateDir;
  private final BundleContext namedBundleContext;
  
  private final IDefaultTemplateLoader defaultTemplateLoader;
  
  private List<TokenParser> tokenParsers = null;
  
  private Map<String, Object> globalVariables = new HashMap<>();
  
  private PebbleEngine engine;
  

  public TemplateEngine (BundleContext namedBundleContext, IDefaultTemplateLoader defaultTemplateLoader) {
//    this.templateDir = templateDir;
    this.namedBundleContext = namedBundleContext;
    this.defaultTemplateLoader = defaultTemplateLoader;
  }
  
  
  @Override
  public void addTokenParser (TokenParser tokenParser) {
    if (tokenParsers == null) {
      tokenParsers = new ArrayList<>();
    }
    tokenParsers.add(tokenParser);
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
          
          // Add extensions
          globalVariables.put("engine", this);
          
          Extension extension = new AbstractExtension() {
            @Override
            public List<TokenParser> getTokenParsers () {
              return tokenParsers;
            }
            
            @Override
            public Map<String, Object> getGlobalVariables () {
              return globalVariables;
            }
          };
          builder.extension(extension);

          // Add bundle specific loader
          Loader<?> loader = new MultiLoader(namedBundleContext, defaultTemplateLoader);
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
