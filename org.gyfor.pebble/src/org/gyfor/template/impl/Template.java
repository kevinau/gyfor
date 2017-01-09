package org.gyfor.template.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.template.ITemplate;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class Template implements ITemplate {

  private final PebbleTemplate template;
  
  private final Map<String, Object> context = new HashMap<>();
  
  
  public Template (PebbleTemplate template) {
    this.template = template;
  }
  
  
  @Override
  public void clearContext () {
    context.clear();
  }
  
  
  @Override
  public void putContext (String name, Object value) {
    context.put(name, value);
  }
  
  
  @Override
  public void evaluate (Writer writer) {
    try {
      template.evaluate(writer, context);
    } catch (PebbleException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void evaluate (Writer writer, Map<String, Object> context) {
    try {
      template.evaluate(writer, context);
    } catch (PebbleException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
