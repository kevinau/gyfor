package org.gyfor.websocket.dao;

import java.util.Arrays;

public class Request {

  private static final char DELIMITER = '|';
  
  private final String name;
  private final String[] args;
  
  
  public Request (String msg) {
    int n = msg.indexOf(DELIMITER);
    if (n == -1) {
      name = msg;
      args = null;
    } else {
      name = msg.substring(0, n);
      
      // Count the number of arguments (the number of delimiters + 1)
      int argn = 1;
      int n1 = msg.indexOf(DELIMITER, n + 1);
      while (n1 != -1) {
        argn++;
        n1 = msg.indexOf(DELIMITER, n1 + 1);
      }

      // Get the arguments
      args = new String[argn];
      int i = 0;
      int n0 = n + 1;
      n1 = msg.indexOf(DELIMITER, n0);
      while (n1 != -1) {
        args[i++] = msg.substring(n0, n1);
        n0 = n1 + 1;
        n1 = msg.indexOf(DELIMITER, n0);
      }
      args[i] = msg.substring(n0); 
    }
  }
  
  
  public String getName () {
    return name;
  }
  
  
  public String[] getArgs() {
    return args;
  }
  
  
  @Override
  public String toString() {
    return "Request [" + name + ", " + Arrays.toString(args) + "]";
  }


  public static void main (String[] argsx) {
    String[] testData = {
        "add\tthe\tqick\tbrown\tfox",
        "add\tdata",
        "add",
    };
    
    for (String data : testData) {
      Request req = new Request(data);
      System.out.println(req.getName());
      String[] args = req.getArgs();
      if (args != null) {
        for (String arg : args) {
          System.out.println(">> " + arg);
        }
      }
    }  
  }
}
