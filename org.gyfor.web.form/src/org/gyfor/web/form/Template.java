package org.gyfor.web.form;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;


public class Template {

  private final PebbleTemplate template;
  
  
  public Template (PebbleTemplate template) {
    this.template = template;
  }
  
  
  public void evaluate (Writer writer) {
    try {
      template.evaluate(writer);
    } catch (PebbleException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public void evaluate (Writer writer, Map<String, Object> context) {
    try {
      template.evaluate(writer, context);
    } catch (PebbleException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
