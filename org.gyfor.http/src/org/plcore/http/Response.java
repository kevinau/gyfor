package org.plcore.http;


public class Response {

  private static final char DELIMITER = '|';
  
  private final String name;
  private final Object[] args;
  
  
  public Response (String name, Object... args) {
    this.name = name;
    this.args = args;
  }
  
  
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(name);
    for (Object arg : args) {
      buffer.append(DELIMITER);
      buffer.append(arg);
    }
    return buffer.toString();
  }
  
  
  public static void main (String[] argsx) {
    Response[] testData = {
        new Response("add", "the", "quick", "brown", "fox"),
        new Response("add", "data"),
        new Response("add"),
    };

    for (Response test : testData) {
      System.out.println(test);
    }
    
  }
  
}
