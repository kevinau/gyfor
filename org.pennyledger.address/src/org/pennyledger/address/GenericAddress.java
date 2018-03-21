package org.pennyledger.address;

import org.plcore.userio.IOField;
import org.plcore.userio.Occurs;
import org.plcore.value.ICode;

public class GenericAddress implements IAddress {

  @IOField
  @Occurs(max = 4)
  private String[] address;
  
  
  public String[] getAddress() {
    return address;
  }

  
  public void setAddress(String[] address) {
    this.address = address;
  }

  
  @Override
  public String[] getFormatted(ICode localCountry) {
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
