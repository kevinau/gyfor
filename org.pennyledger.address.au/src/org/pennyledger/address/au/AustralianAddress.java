package org.pennyledger.address.au;

import org.gyfor.object.Occurs;
import org.pennyledger.address.ICountryAddress;

public class AustralianAddress implements ICountryAddress {

  private String[] addressLines;
  
  private String townSuburb;
  
  private AustralianState state;
  
  private String postcode;
  
  
  public String getTownSuburb() {
    return townSuburb;
  }

  
  public void setTownSuburb(String townSuburb) {
    this.townSuburb = townSuburb;
  }

  
  public AustralianState getState() {
    return state;
  }

  
  public void setState(AustralianState state) {
    this.state = state;
  }

  
  public String getPostcode() {
    return postcode;
  }

  
  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  
  @Occurs(max = 2)
  public void setAddressLines(String[] addressLines) {
    this.addressLines = addressLines;
  }

  @Override
  public String getCountry() {
    return "AU";
  }

  @Override
  public String[] getAddressLines() {
    int n = 0;
    for (String line : addressLines) {
      if (line != null && line.length() > 0) {
        n++;
      }
    }
    String[] lines = new String[n + 1];
    int i = 0;
    for (String line : addressLines) {
      if (line != null && line.length() > 0) {
        lines[i++] = line;
      }
    }
    lines[i++] = townSuburb + " " + state + " " + postcode;
    return lines;
  }

}
