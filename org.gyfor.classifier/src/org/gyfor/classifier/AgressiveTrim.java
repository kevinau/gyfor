package org.gyfor.classifier;


public class AgressiveTrim {

  public static String trim (String s) {
    char[] cx = s.toCharArray();
    int i = 0;
    while (i < cx.length) {
      if (Character.isLetterOrDigit(cx[i])) {
        break;
      }
      i++;
    }
    int j = cx.length;
    while (i < j) {
      if (Character.isLetterOrDigit(cx[j - 1])) {
        break;
      }
      j--;
    }
    if (i == 0 && j == cx.length) {
      return s;
    } else {
      return s.substring(i, j);
    }
  }
  
  
  public static void main(String[] args) {
    String[] x = {
        "",
        "abc",
        "...abc",
        "abc;;;",
        "...abc;;;",
        "......",
    };
    
    for (String xx : x) {
      System.out.println(xx + "   <" + AgressiveTrim.trim(xx) + ">");
    }
  }
}
