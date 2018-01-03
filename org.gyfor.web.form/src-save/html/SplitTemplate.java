package org.gyfor.web.form.html;

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;

public class SplitTemplate {

  private PebbleTemplate beforeTemplate;
  
  private PebbleTemplate afterTemplate;
  
  private Method evaluateMethod;
  
  
  SplitTemplate (PebbleTemplate beforeTemplate, PebbleTemplate afterTemplate) {
    this.beforeTemplate = beforeTemplate;
    this.afterTemplate = afterTemplate;
    
    try {
      evaluateMethod = PebbleTemplateImpl.class.getDeclaredMethod("evaluate", Writer.class, EvaluationContext.class);
    } catch (NoSuchMethodException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public void evaluateBefore (Writer writer, EvaluationContext context) {
    try {
      if (beforeTemplate != null) {
        // This bad code is because the evaluate method is private in PebbleTemplateImpl 
        evaluateMethod.invoke(beforeTemplate, writer, context);
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public void evaluateAfter (Writer writer, EvaluationContext context) {
    try {
      if (afterTemplate != null) {
        // This bad code is because the evaluate method is private in PebbleTemplateImpl 
        evaluateMethod.invoke(afterTemplate, writer, context);
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

}
