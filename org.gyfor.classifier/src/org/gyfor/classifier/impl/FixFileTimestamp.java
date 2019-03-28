package org.gyfor.classifier.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FixFileTimestamp {

  public static void main (String[] args) throws Exception {
    SimpleDateFormat parser=new SimpleDateFormat("yyyy_MM_dd");

    File dir = new File("C:/Users/Kevin/inbox");
    String[] names = dir.list();
    for (String name : names) {
      int n = name.indexOf("_20");
      String datex = name.substring(n + 1, n + 1 + 10);
      Date date = parser.parse(datex);
      long dx = date.getTime() + (int)(9.5 * 60 * 60 * 1000);
      File file = new File(dir, name);
      file.setLastModified(dx);
    }
  }
}
