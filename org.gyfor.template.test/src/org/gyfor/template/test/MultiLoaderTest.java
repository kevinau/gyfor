package org.gyfor.template.test;

import java.io.StringWriter;

import org.gyfor.template.ITemplate;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component
public class MultiLoaderTest {

  private ITemplateEngineFactory engineFactory;
  
  @Reference
  public void setTemplateEngineFactory (ITemplateEngineFactory engineFactory) {
    this.engineFactory = engineFactory;
  }
  
  
  public void unsetTemplateEngineFactory (ITemplateEngineFactory engineFactory) {
    this.engineFactory = null;
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    ITemplateEngine engine = engineFactory.buildTemplateEngine(bundleContext);
    
    String[] templateNames = {
        "page",
        "#field1(page)",
        "org.gyfor.template.test.TestClass#field2(page)",
        "org.gyfor.template.test.TestClass(page)",
        "org.gyfor.template.test.XXXXX(page)",
    };
    
    for (String tn : templateNames) {
      System.out.println();
      System.out.println("Looking for template: " + tn);
      ITemplate template = engine.getTemplate(tn);
      StringWriter writer = new StringWriter();
      template.evaluate(writer);
      System.out.println(">>> " + writer.toString());
    }
  }

}
