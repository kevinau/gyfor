package org.pennyledger.web.about;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.pennyledger.template.ITemplate;
import org.pennyledger.template.ITemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = Servlet.class, 
           property = { "osgi.http.whiteboard.servlet.pattern=/about",
                        "servlet.init.myname=About Servlet" })
public class AboutServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(AboutServlet.class);

  private ITemplateService templateService;

  private ITemplate template;


  @Reference
  protected void setTemplateService(ITemplateService templateService) {
    logger.info("Setting template service: " + templateService);
    System.out.println("Setting template service: " + templateService);
    this.templateService = templateService;
  }


  protected void unsetTemplateService(ITemplateService templateService) {
    this.templateService = null;
  }


  @Activate
  public void activate(ComponentContext componentContext) {
    logger.info("Activate: " + this.getClass());

    BundleContext bundleContext = componentContext.getBundleContext();
    template = templateService.createTemplate(bundleContext, "templates/about.html");

    Bundle bundle = componentContext.getBundleContext().getBundle();
    URL resourceURL = bundle.getResource("/");
    logger.info("Template resource root {}", resourceURL);
    logger.info("Template class {}", this.getClass().getName());
  }

  
  @Deactivate
  public void deactivate() {
    logger.info("Deactivate: " + this.getClass());
    template = null;
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    logger.info("Processing GET: " + req.getRequestURI());
    
    if (template == null) {
      throw new RuntimeException("No template for this servlet");
    }

    template.setParameter("docbase", req.getContextPath());
    Map<String, String> requests = new HashMap<>();
    for (Enumeration<String> e = req.getParameterNames(); e.hasMoreElements();) {
      String name = e.nextElement();
      String value = req.getParameter(name);
      requests.put(name, value + "");
    }
    template.setParameter("requestParams", requests);
    // template.setParameter("request", request);
    template.merge(resp.getWriter());
    resp.setStatus(HttpServletResponse.SC_OK);
  }

}
