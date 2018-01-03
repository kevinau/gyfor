package org.gyfor.web.form.defn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


public class BundleContextPath {

  private final BundleContext bundleContext;
  private final String prefixDir;

  private String charset = StandardCharsets.UTF_8.name();


  public BundleContextPath(BundleContext bundleContext) {
    this(bundleContext, null);
  }


  public BundleContextPath(BundleContext bundleContext, String prefixDir) {
    this.bundleContext = bundleContext;
    this.prefixDir = prefixDir;
  }


  private String getPrefixedName(String templateName) {
    if (prefixDir == null) {
      return templateName;
    } else {
      return prefixDir + "/" + templateName;
    }
  }


  public Reader getReader(String templateName) {
    InputStream is = getInputStream(templateName);
    try {
      return new InputStreamReader(is, charset);
    } catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }


  public BufferedReader getBufferedReader(String templateName) {
    Reader reader = getReader(templateName);
    return new BufferedReader(reader);
  }


  public boolean exists(String templateName) {
    Bundle bundle = bundleContext.getBundle();
    String prefixedName = getPrefixedName(templateName);
    URL templateURL = bundle.getResource(prefixedName);
    return templateURL != null;
  }


  public InputStream getInputStream(String templateName) {
    Bundle bundle = bundleContext.getBundle();

    String prefixedName = getPrefixedName(templateName);
    URL templateURL = bundle.getResource(prefixedName);
    if (templateURL == null) {
      throw new IllegalArgumentException(
          "File '" + prefixedName + "' not found in bundle: " + bundle.getSymbolicName());
    }
    InputStream is;
    try {
      is = templateURL.openStream();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    return is;
  }

}
