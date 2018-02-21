package org.pennyledger.address;

import org.gyfor.object.Occurs;

public class GenericAddress implements IAddress {

  private String[] address;
  
  
  public String[] getAddress() {
    return address;
  }

  
  @Occurs(max = 3)
  public void setAddress(String[] address) {
    this.address = address;
  }

  
  @Override
  public String[] getAddressLines() {
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

}
