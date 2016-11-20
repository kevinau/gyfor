package org.gyfor.pebble.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.gyfor.pebble.ITemplate;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class Template implements ITemplate {

  private final PebbleTemplate template;
  
  
  public Template (PebbleTemplate template) {
    this.template = template;
  }
  
  
  @Override
  public void evaluate (Writer writer) {
    try {
      template.evaluate(writer);
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
