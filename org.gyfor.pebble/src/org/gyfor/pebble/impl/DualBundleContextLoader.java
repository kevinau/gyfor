package org.gyfor.pebble.impl;

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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;


public class DualBundleContextLoader implements Loader<String> {

  private Logger logger = LoggerFactory.getLogger(DualBundleContextLoader.class);

  private final BundleContext globalBundleContext;
  private final BundleContext primaryBundleContext;

  private final String prefixDir = "/templates";
  private final String suffix = ".html";

  private String charset = StandardCharsets.UTF_8.name();


  public DualBundleContextLoader(BundleContext primaryBundleContext, BundleContext globalBundleContext) {
    this.primaryBundleContext = primaryBundleContext;
    this.globalBundleContext = globalBundleContext;
  }


  public DualBundleContextLoader(BundleContext primaryBundleContext) {
    this(primaryBundleContext, null);
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
    
    bundle = primaryBundleContext.getBundle();
    templateURL = findTemplate(bundle, templateName, tried);
    
    if (templateURL == null && globalBundleContext != null) {
      // Look for templates in the global bundle
      bundle = globalBundleContext.getBundle();
      templateURL = findTemplate(bundle, templateName, tried);
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

}
