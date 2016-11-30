package org.gyfor.template;

import java.io.Writer;
import java.util.Map;

public interface ITemplate {

  public void evaluate(Writer writer);

  public void evaluate(Writer writer, Map<String, Object> context);

}
