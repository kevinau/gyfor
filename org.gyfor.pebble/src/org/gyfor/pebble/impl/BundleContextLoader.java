package org.gyfor.pebble.impl;

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

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;


public class BundleContextLoader implements Loader<String> {

  private final BundleContext bundleContext;
  private final String prefixDir;
  private final String suffix;
  
  private String charset = StandardCharsets.UTF_8.name();
  
  
  public BundleContextLoader (BundleContext bundleContext) {
    this (bundleContext, "/templates");
  }
  
  
  public BundleContextLoader (BundleContext bundleContext, String prefixDir) {
    this (bundleContext, prefixDir, ".html");
  }
  

  public BundleContextLoader (BundleContext bundleContext, String prefixDir, String suffix) {
    this.bundleContext = bundleContext;
    this.prefixDir = prefixDir;
    this.suffix = suffix;
  }
  

  @Override
  public String createCacheKey(String templateName) {
    if (prefixDir == null) {
      return templateName;
    } else {
      return prefixDir + "/" + templateName;
    }
  }

  
  @Override
  public Reader getReader(String templateName) throws LoaderException {
    return getBufferedReader(templateName);
  }
  
  
  public BufferedReader getBufferedReader(String templateName) throws LoaderException {
    Bundle bundle = bundleContext.getBundle();
  
    URL templateURL = bundle.getResource(templateName + suffix);
    if (templateURL == null) {
      throw new IllegalArgumentException("Template '" + templateName + suffix + "' not found in bundle: " + bundle.getSymbolicName());
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

  
  public boolean exists(String templateName) {
    Bundle bundle = bundleContext.getBundle();
    String templateFile = createCacheKey(templateName) + suffix;
    URL templateURL = bundle.getResource(templateFile);
    return templateURL != null;
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
