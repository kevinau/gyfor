package org.plcore.template;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.mitchellbosecke.pebble.template.ScopeChain;


public interface ITemplate {

  public void putContext (String name, Object value);
  
  public void clearContext ();
  
  public void evaluate (Writer writer);
  
  public ScopeChain evaluate2(Writer writer, Map<String, Object> map);

  public default String evaluate () {
    StringWriter writer = new StringWriter();
    evaluate (writer);
    return writer.toString();
  }
  
  public void evaluate(Writer writer, Map<String, Object> context);

}
