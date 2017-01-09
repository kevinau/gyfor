package org.gyfor.template;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public interface ITemplate {

  public void putContext (String name, Object value);
  
  public void clearContext ();
  
  public void evaluate (Writer writer);
  
  public default String evaluate () {
    StringWriter writer = new StringWriter();
    evaluate (writer);
    return writer.toString();
  }
  
  public void evaluate(Writer writer, Map<String, Object> context);

}
