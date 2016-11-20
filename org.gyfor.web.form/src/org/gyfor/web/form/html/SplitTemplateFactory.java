package org.gyfor.web.form.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.web.form.defn.BundleContextPath;
import org.osgi.framework.BundleContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class SplitTemplateFactory {

  // TODO.  Make this a synchronized map
  private final Map<String, SplitTemplate> templateCache = new HashMap<>();
  
  private final BundleContextPath path;
 
  public SplitTemplateFactory (BundleContext bundleContext, String templateDir) {
    // For loading the template text
    this.path = new BundleContextPath(bundleContext, templateDir);
  }

  
  public SplitTemplate get (PebbleEngine pebbleEngine, String fileName) {
    SplitTemplate template = templateCache.get(fileName);
    try {
      BufferedReader reader = path.getBufferedReader(fileName);
      
      // Get the before and after Strings
      StringBuilder beforeText = new StringBuilder();
      StringBuilder afterText = null;
      
      Pattern rep = Pattern.compile("\\{% *innerContent *%\\}");
      String line = reader.readLine();
      while (line != null) {
        Matcher matcher = rep.matcher(line);
        if (matcher.find()) {
          String x = line.substring(0, matcher.start());
          String y = line.substring(matcher.end());
          beforeText.append(x);
          afterText = new StringBuilder(y);
          line = reader.readLine();
          while (line != null) {
            afterText.append(line);
            line = reader.readLine();
          }
          break;
        }
        beforeText.append(line);
        line = reader.readLine();  
      }
      
      // Compile the templates
      PebbleTemplate beforeTemplate = pebbleEngine.getTemplate(beforeText.toString());
      PebbleTemplate afterTemplate = null;
      if (afterText != null) {
        afterTemplate = pebbleEngine.getTemplate(afterText.toString());
      }
      template = new SplitTemplate(beforeTemplate, afterTemplate);
      templateCache.put(fileName, template);
    } catch (IOException | PebbleException ex) {
      throw new RuntimeException(ex);
    }
    return template;
  }

}
