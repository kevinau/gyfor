package org.gyfor.pebble.impl;

import org.gyfor.gobal.IGlobalContext;
import org.gyfor.pebble.ITemplateEngine;
import org.gyfor.pebble.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component
public class TemplateEngineFactory implements ITemplateEngineFactory {

  private IGlobalContext globalContext;
  
  
  @Reference (cardinality=ReferenceCardinality.OPTIONAL)
  public void setGlobalContext (IGlobalContext globalContext) {
    this.globalContext = globalContext;
  }
  
  
  public void unsetGlobalContext (IGlobalContext globalContext) {
    this.globalContext = null;
  }
  
  
  @Override
  public ITemplateEngine buildTemplateEngine(BundleContext primaryBundleContext) {
    return new TemplateEngine(primaryBundleContext, globalContext.getBundleContext());
  }

}
