package org.plcore.template.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class CapturingTemplate implements PebbleTemplate {

  private final PebbleTemplate wrapped;
  
  public CapturingTemplate (PebbleTemplate wrapped) {
    this.wrapped = wrapped; 
  }
  
  @Override
  public void evaluate(Writer writer) throws PebbleException, IOException {
    wrapped.evaluate(writer);
  }

  @Override
  public void evaluate(Writer writer, Locale locale) throws PebbleException, IOException {
    wrapped.evaluate(writer, locale);
  }

  @Override
  public void evaluate(Writer writer, Map<String, Object> additional) throws PebbleException, IOException {
    wrapped.evaluate(writer, additional);
  }

  @Override
  public void evaluate(Writer writer, Map<String, Object> additional, Locale locale) throws PebbleException, IOException {
    wrapped.evaluate(writer, additional, locale);
    
  }

  @Override
  public void evaluateBlock(String blockName, Writer writer) throws PebbleException, IOException {
    wrapped.evaluateBlock(blockName, writer);
  }

  @Override
  public void evaluateBlock(String blockName, Writer writer, Locale locale) throws PebbleException, IOException {
    wrapped.evaluateBlock(blockName, writer, locale);
  }

  @Override
  public void evaluateBlock(String blockName, Writer writer, Map<String, Object> additional) throws PebbleException, IOException {
    wrapped.evaluateBlock(blockName, writer, additional);
  }

  @Override
  public void evaluateBlock(String blockName, Writer writer, Map<String, Object> additional, Locale locale)
      throws PebbleException, IOException {
    wrapped.evaluateBlock(blockName, writer, additional, locale);
  }

  @Override
  public String getName() {
    return wrapped.getName();
  }

}
