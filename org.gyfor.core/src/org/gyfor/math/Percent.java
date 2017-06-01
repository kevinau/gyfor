package org.gyfor.math;


public class Percent {

  private final Decimal value;
  
  public static final Percent ZERO = new Percent(0);
  
  
  public Percent (String source) {
    source = source.trim();
    if (source.endsWith("%")) {
      int n = source.length();
      source = source.substring(0, n - 1);
    }
    value = new Decimal(source);
  }

  
  public Percent (Decimal d) {
    value = d;
  }
  
  
  public Percent (int x) {
    value = new Decimal(x);
  }
  
  
  public Decimal multiply (Decimal amt) {
    return value.multiply(amt).divide(100);
  }


  public Decimal toDecimal() {
    return value;
  }
  
}
