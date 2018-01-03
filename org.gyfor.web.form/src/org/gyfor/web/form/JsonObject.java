package org.gyfor.web.form;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject extends LinkedHashMap<String, Object> {

  private static final long serialVersionUID = 1L;


  public JsonObject (Object... nameValuePairs) {
    int i = 0;
    while (i < nameValuePairs.length) {
      String name = (String)nameValuePairs[i];
      i++;
      put(name, nameValuePairs[i]);
      i++;
    }
  }
  
  
  public JsonObject add (String name, Object value) {
    put(name, value);
    return this;
  }

  
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    toString(buffer);
    return buffer.toString();
  }
  
  
  public void toString(StringBuilder buffer) {
    buffer.append('{');
    int i = 0;
    for (Map.Entry<String, Object> entry : entrySet()) {
      if (i > 0) {
        buffer.append(',');
      }
      buffer.append('"');
      buffer.append(entry.getKey());
      buffer.append('"');
      buffer.append(':');
      Object value = entry.getValue();
      if (value instanceof String) {
        buffer.append('"');
        buffer.append(value);
        buffer.append('"');
      } else if (value instanceof Integer) {
        buffer.append(value.toString());
      } else if (value instanceof Boolean) {
        buffer.append(value.toString().toLowerCase());
      } else if (value instanceof Enum) {
        buffer.append('"');
        buffer.append(value.toString());
        buffer.append('"');
      } else if (value instanceof JsonObject) {
        buffer.append(value.toString());
      } else {
        throw new RuntimeException("Unsupported value type: " + value.getClass().getSimpleName());
      }
      i++;
    }
    buffer.append('}');
  }
  
}
