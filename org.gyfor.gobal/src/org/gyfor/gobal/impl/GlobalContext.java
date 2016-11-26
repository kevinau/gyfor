package org.gyfor.gobal.impl;

import org.gyfor.gobal.IGlobalContext;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component
public class GlobalContext implements IGlobalContext {

  private BundleContext bundleContext;
  
  
  @Activate 
  public void activate (BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }
  
  
  @Deactivate 
  public void deactivate (BundleContext bundleContext) {
    this.bundleContext = null;
  }
  
  
  @Override
  public BundleContext getBundleContext() {
    return bundleContext;
  }

}
