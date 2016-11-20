package org.gyfor.web.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;


/**
 * A loader that searches for and loads entity and field specific templates.
 * <p>
 * The loader expects a entity or field name. A field name contains a '#'
 * character--a entity name does not.
 * <p>
 * If the loader is given an entity name...
 * <p>
 * If the loader is given a field name, it will be in the form <FQ class
 * name>#<<fieldName>, where field name can be a compound name in the form
 * <segment>(.<segment>)*. If the loader finds a template with this name, it is
 * returned. Otherwise, it shortens the FQ class name by removing the last
 * segment, looks again. This process is repeated until the FQ class name is the
 * empty string. A consequence of this process is that fully global field templates
 * must have the name #<fieldName>.
 * <p>
 * The above searching is done in the "templates" directory of the bundle
 * containing the entity class.
 * <p>
 * If the above searching does not find a template, the template named "field"
 * is used.
 */
public class EntityFieldLoader implements Loader<String> {

  private Logger logger = LoggerFactory.getLogger(EntityFieldLoader.class);

  private final BundleContext bundleContext;
  private final String prefixDir;
  private final String suffix;

  private String charset = StandardCharsets.UTF_8.name();


  public EntityFieldLoader(BundleContext bundleContext) {
    this(bundleContext, "/templates");
  }


  public EntityFieldLoader(BundleContext bundleContext, String prefixDir) {
    this(bundleContext, prefixDir, ".html");
  }


  public EntityFieldLoader(BundleContext bundleContext, String prefixDir, String suffix) {
    if (bundleContext == null) {
      throw new IllegalArgumentException("bundle context cannot be null");
    }
    this.bundleContext = bundleContext;
    this.prefixDir = prefixDir;
    this.suffix = suffix;
  }


  @Override
  public String createCacheKey(String templateName) {
    return templateName;
  }


  @Override
  public Reader getReader(String templateName) throws LoaderException {
    return getBufferedReader(templateName);
  }


  private URL getReducingURL (Bundle bundle, String className, String fieldName) {
    URL templateURL = bundle.getResource(prefixDir + "/" + className + fieldName + suffix);
    if (templateURL != null) {
      return templateURL;
    }
    if (className.length() == 0) {
      return null;
    }
    int n = className.lastIndexOf('.');
    if (n >= 0) {
      className = className.substring(0, n);
    } else {
      className = "";
    }
    return getReducingURL (bundle, className, fieldName);
  }


  private Bundle getClassBundle (String className) {
    Class<?> klass;
    try {
      klass = Class.forName(className);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    logger.info("Using class {}, which translates to class {}", className, klass);
    Bundle bundle = FrameworkUtil.getBundle(klass);
    logger.info("Found bundle {}", bundle.getSymbolicName());
    return bundle;
  }
  

  private URL getDefaultTemplate (String originalName, String name) throws LoaderException {
    // Look for a default template
    Bundle bundle = bundleContext.getBundle();
    String fullTemplateName = prefixDir + "/" + name + suffix;
    URL templateURL = bundle.getResource(fullTemplateName);
    if (templateURL == null) {
      throw new LoaderException(null, "No template '" + originalName + "' in class path, or '" + name + "' in bundle: " + bundle);
    }
    return templateURL;
  }
  
  
  public BufferedReader getBufferedReader(String templateName) throws LoaderException {
    Bundle bundle = null;
    URL templateURL;
    
    int n = templateName.indexOf('#');
    if (n >= 0) {
      // The template name is a field name
      logger.info("Looking for field template '{}'", templateName);
      String className = templateName.substring(0, n);
      bundle = getClassBundle(className);
      String fieldName = templateName.substring(n);
      templateURL = getReducingURL(bundle, className, fieldName);
      if (templateURL == null) {
        templateURL = getDefaultTemplate(templateName, "field");
      }
    } else if (templateName.contains(".")) {
      // The template name is a fully qualified class name
      logger.info("Looking for entity template '{}'", templateName);

      bundle = getClassBundle(templateName);
      templateURL = bundle.getResource(prefixDir + "/" + templateName + suffix);
      logger.info("Found URL {}, based on {}", templateURL, prefixDir + "/" + templateName + suffix);
      if (templateURL == null) {
        templateURL = getDefaultTemplate(templateName, "entity");        
      }
    } else {
      logger.info("Looking for default template '{}'", templateName);
      bundle = bundleContext.getBundle();
      templateURL = bundle.getResource(prefixDir + "/" + templateName + suffix);
      if (templateURL == null) {
        throw new LoaderException(null, "No template '" + templateName + "' in bundle: " + bundle);
      }
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


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + bundleContext.hashCode();
    result = prime * result + ((prefixDir == null) ? 0 : prefixDir.hashCode());
    result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    EntityFieldLoader other = (EntityFieldLoader)obj;
    if (!bundleContext.equals(other.bundleContext)) {
      return false;
    }
    if (prefixDir == null) {
      if (other.prefixDir != null) {
        return false;
      }
    } else if (!prefixDir.equals(other.prefixDir)) {
      return false;
    }
    if (suffix == null) {
      if (other.suffix != null) {
        return false;
      }
    } else if (!suffix.equals(other.suffix)) {
      return false;
    }
    return true;
  }

  
  
}
