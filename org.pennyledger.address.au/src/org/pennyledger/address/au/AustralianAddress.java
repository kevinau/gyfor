package org.pennyledger.address.au;

import org.osgi.service.component.annotations.Component;
import org.pennyledger.address.ICountryAddress;
import org.plcore.userio.IOField;
import org.plcore.userio.Occurs;
import org.plcore.value.Code;
import org.plcore.value.ICode;

@Component
public class AustralianAddress implements ICountryAddress {

  private static final ICode auCountry = new Code("AU", "Australia");
  
  @IOField
  @Occurs(max = 2)
  private String[] addressLines;
  
  @IOField
  private String townSuburb;
  
  @IOField
  private AustralianState state;
  
  @IOField
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

  
  public void setAddressLines(String[] addressLines) {
    this.addressLines = addressLines;
  }

  @Override
  public ICode getCountry() {
    return auCountry;
  }

  @Override
  public String[] getFormatted(ICode localCountry) {
    int n = 0;
    for (String line : addressLines) {
      if (line != null && line.length() > 0) {
        n++;
      }
    }
    if (!auCountry.equals(localCountry)) {
      n++;
    }
    String[] lines = new String[n + 1];
    int i = 0;
    for (String line : addressLines) {
      if (line != null && line.length() > 0) {
        lines[i++] = line;
      }
    }
    lines[i++] = townSuburb + " " + state + " " + postcode;
    if (!auCountry.equals(localCountry)) {
      lines[i++] = auCountry.getDescription().toUpperCase();
    }
    return lines;
  }

}
