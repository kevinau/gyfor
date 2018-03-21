package org.plcore.template.impl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.plcore.template.ITemplate;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;

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
  public ScopeChain evaluate2 (Writer writer, Map<String, Object> map) {
    EvaluationContext evalContext;
    try {
      Class<?> implClass = PebbleTemplateImpl.class;
      Method privateEval = implClass.getDeclaredMethod("evaluate", Writer.class, EvaluationContext.class);
      privateEval.setAccessible(true);
      
      Method privateInitContext = implClass.getDeclaredMethod("initContext", Locale.class);
      privateInitContext.setAccessible(true);
      evalContext = (EvaluationContext)privateInitContext.invoke(template, (Locale)null);
      evalContext.getScopeChain().pushScope(map);

      privateEval.invoke(template, writer, evalContext);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException 
        | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
    return evalContext.getScopeChain();
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
