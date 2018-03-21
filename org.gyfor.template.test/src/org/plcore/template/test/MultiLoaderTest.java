package org.plcore.template.test;

import java.io.StringWriter;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.template.ITemplate;
import org.plcore.template.ITemplateEngine;
import org.plcore.template.ITemplateEngineFactory;


@Component
public class MultiLoaderTest {

  @Reference
  private ITemplateEngineFactory engineFactory;
  
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
