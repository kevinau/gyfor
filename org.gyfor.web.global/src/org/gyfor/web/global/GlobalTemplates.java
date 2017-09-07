package org.gyfor.web.global;

import java.net.URL;
import java.util.List;

import org.gyfor.template.IDefaultTemplateLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;


@Component (immediate=true)
public class GlobalTemplates implements IDefaultTemplateLoader {

  private final String bundleDir = "/templates";
  private final String suffix = ".html";

  private BundleContext bundleContext;
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }
  
  
  @Override
  public URL findTemplate(String templateName, List<String> tried) {
    String fileName = bundleDir + "/" + templateName + suffix;

    Bundle bundle = bundleContext.getBundle();
    URL url = bundle.getResource(fileName);
    if (url == null) {
      tried.add(bundle.getSymbolicName() + "::" + fileName);
    }
    return url;
  }

}
