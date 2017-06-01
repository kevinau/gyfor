package org.gyfor.web.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;


/**
 * A template loader that searches multiple bundle contexts to locate a
 * template.
 * <p>
 * The template name is made up of: <blockquote>
 * &lt;classname&gt;#&lt;fieldname&gt;(&lt;defaultname&gt;) </blockquote> The
 * &lt;classname&gt; and the #&lt;fieldname&gt; are both optional. The
 * (&lt;defaultname&gt;) is required.
 * <p>
 * The &lt;classname&gt;#&lt;fieldname&gt; is the canonical path of the model
 * for which a template is required. The &lt;classname&gt; is the parent entity
 * of the model, while #&lt;fieldname&gt; is the canonical path of a node model
 * within the entity. The &lt;defaultname&gt; can be derived from the type of
 * the node model. For entities and container nodes, the simple class name of
 * the node is used for the default name. For item nodes, the simple class name
 * of the node's type is used.
 * <p>
 * This template loader looks in the following bundles, in the order listed:
 * <ol>
 * <li>If &lt;classname&gt; only is specified, and the class is loadable, this
 * loader searches the bundle of the loaded class for a template named
 * &lt;classname&gt;.</li>
 * <li>If &lt;classname&gt;#&lt;fieldname&gt; is specified, and the named class
 * is loadable, this loader searches the bundle of the loaded class for a
 * template named:
 * <ul>
 * <li>&lt;classname&gt;#&lt;fieldname&gt;</li>
 * <li>#&lt;fieldname&gt;.</li>
 * </ul>
 * </li>
 * <li>If an IGlobalTemplate component exists, it returns a bundle context. This
 * bundle is searched for templates in the same way as 1 and 2 above.</li>
 * <li>A default template is searched for in the default bundle context.</li>
 * </ol>
 */
public class MultiLoader implements Loader<String> {

  private Logger logger = LoggerFactory.getLogger(MultiLoader.class);

  // <defaultname>
  private static final String simpleRegex = "\\w+";

  // <classname>#<fieldname>(<defaultname>)
  private static final String templateRegex = "(\\w+(?:\\.\\w+)*)?(#\\w+(?:\\.\\w+)*)?\\((\\w+)\\)";

  private static final Pattern simplePattern = Pattern.compile(simpleRegex);
  private static final Pattern templatePattern = Pattern.compile(templateRegex);

  private final BundleContext localBundleContext;
  private final BundleContext globalBundleContext;

  private final String prefixDir = "/templates";
  private final String suffix = ".html";

  private String charset = StandardCharsets.UTF_8.name();


  public MultiLoader(BundleContext localBundleContext, BundleContext globalBundleContext) {
    this.localBundleContext = localBundleContext;
    this.globalBundleContext = globalBundleContext;
  }


  public MultiLoader(BundleContext defaultBundleContext) {
    this(defaultBundleContext, null);
  }


  @Override
  public String createCacheKey(String templateName) {
    return templateName;
  }


  @Override
  public Reader getReader(String templateName) throws LoaderException {
    URL templateURL = null;
    Bundle bundle = null;
    // The following is for the error message if the template cannot be found
    List<String> tried = new ArrayList<>(5);
    
    Matcher matcher = simplePattern.matcher(templateName);
    if (matcher.matches()) {
      if (localBundleContext != null) {
        // Look for templates in the local bundle
        bundle = localBundleContext.getBundle();
        templateURL = findTemplate(bundle, templateName, tried);
      }
      if (templateURL == null) {
        // Look for templates in the global bundle
        bundle = globalBundleContext.getBundle();
        templateURL = findTemplate(bundle, templateName, tried);
      }
    } else {
      matcher = templatePattern.matcher(templateName);
      if (!matcher.matches()) {
        throw new LoaderException(null, "Template name '" + templateName
            + "' does not match: <classname>#<fieldname>(<defaultname>) or <defaultname>");
      }
      String className = matcher.group(1);
      String fieldName = matcher.group(2);
      if (className == null && fieldName == null) {
        throw new LoaderException(null, "Template name '" + templateName
            + "' does not match: <classname>#<fieldname>(<defaultname>) or <defaultname>");
      }
      String defaultName = matcher.group(3);
      if (defaultName.equals("entity") && fieldName != null) {
        throw new LoaderException(null, "Template name of an entity cannot contain a field name");
      }

      if (className != null) {
        // We assume the template name is a fully qualified class name
        Class<?> klass;
        try {
          klass = Class.forName(className);
        } catch (ClassNotFoundException ex) {
          throw new RuntimeException(ex);
        }
        // Look for templates in the bundle of the class
        bundle = FrameworkUtil.getBundle(klass);
        templateURL = findTemplate(bundle, className, fieldName, tried);
      }

      if (templateURL == null && localBundleContext != null) {
        // Look for templates in the local bundle
        bundle = localBundleContext.getBundle();
        templateURL = findTemplate(bundle, className, fieldName, tried);
        if (templateURL == null) {
          // Look for default template in the local bundle
          templateURL = findTemplate(bundle, defaultName, tried);
        }
      }

      if (templateURL == null && globalBundleContext != null) {
        // Look for templates in the global bundle
        bundle = globalBundleContext.getBundle();
        templateURL = findTemplate(bundle, defaultName, tried);
      }
    
    }
    
    if (templateURL == null) {
      String msg = "Template not found after trying:";
      String x = " ";
      for (String s : tried) {
        msg += x + s;
        x = ", ";
      }
      throw new LoaderException(null, msg);
    }
    
    BufferedReader reader;
    try {
      InputStream is = templateURL.openStream();
      InputStreamReader isr = new InputStreamReader(is, charset);
      reader = new BufferedReader(isr);
    } catch (IOException ex) {
      throw new LoaderException(ex, templateName);
    }
    return reader;
  }


  private URL findTemplate(Bundle bundle, String baseName, List<String> tried) {
    String templateName = prefixDir + "/" + baseName + suffix;
    URL url = bundle.getResource(templateName);
    if (url != null) {
      logger.info("Found template '" + templateName + "' in bundle: " + bundle.getSymbolicName());
    } else {
      tried.add(bundle.getSymbolicName() + "::" + templateName);
    }
    return url;
  }


  private URL findTemplate(Bundle bundle, String className, String fieldPath, List<String> tried) {
    if (className == null) {
      return findTemplate(bundle, fieldPath, tried);
    } else if (fieldPath == null) {
      return findTemplate(bundle, className, tried);
    } else {
      URL url = findTemplate(bundle, className + fieldPath, tried);
      if (url != null) {
        return url;
      }
      return findTemplate(bundle, fieldPath, tried);
    }
  }


  @Override
  public String resolveRelativePath(String templateName, String parentName) {
    if (templateName.startsWith("/")) {
      return templateName;
    }
    File parent = new File(parentName).getParentFile();
    if (parent == null) {
      return templateName;
    } else {
      return new File(parent, templateName).toString();
    }
  }


  @Override
  public void setCharset(String charset) {
    this.charset = charset;
  }


  @Override
  public void setPrefix(String arg0) {
    throw new RuntimeException("Prefixes cannot be used in the BundleContextLoader");
  }


  @Override
  public void setSuffix(String arg0) {
    throw new RuntimeException("Suffixes cannot be used in the BundleContextLoader");
  }


  public static void main(String[] args) {
    String[] xx = { 
        "org.pennyledger.Person(entity)", 
        "#name(StringType)", 
        "#address.line1(StringType)",
        "org.pennyledger.Person#emailAddress(StringType)", 
        "org.pennyledger.Person#emailAddress.au(StringType)",
    };

    Pattern p = Pattern.compile(templateRegex);
    for (String x : xx) {
      Matcher m = p.matcher(x);
      System.out.print(x + ": ");
      for (int i = 1; i < m.groupCount() + 1; i++) {
        System.out.print("/" + m.group(i));
      }
      System.out.println();
    }
  }
}
