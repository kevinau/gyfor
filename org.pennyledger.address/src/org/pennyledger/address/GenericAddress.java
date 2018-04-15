package org.pennyledger.address;

import org.plcore.userio.Occurs;

public class GenericAddress implements ILocalizedAddress {

  @Occurs(4)
  private String[] address;
  
  
  public String[] getAddress() {
    return address;
  }

  
  public void setAddress(String[] address) {
    this.address = address;
  }

  
  @Override
  public String[] getFormatted() {
    int n = 0;
    for (String line : address) {
      if (line != null && line.length() > 0) {
        n++;
      }
    }
    String[] linex = new String[n];
    int i = 0;
    for (String line : address) {
      if (line != null && line.length() > 0) {
        linex[i++] = line;
      }
    }
    return linex;
  }


  @Override
  public Country getCountry() {
    return null;
  }

}
