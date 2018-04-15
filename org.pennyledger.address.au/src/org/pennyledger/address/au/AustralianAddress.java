package org.pennyledger.address.au;

import org.osgi.service.component.annotations.Component;
import org.pennyledger.address.Country;
import org.pennyledger.address.ILocalizedAddress;
import org.plcore.userio.Embeddable;
import org.plcore.userio.Occurs;

@Component
@Embeddable
public class AustralianAddress implements ILocalizedAddress {

  private static final Country country = new Country("AU", "Australia");
  
  @Occurs(2)
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

  
  public void setAddressLines(String[] addressLines) {
    this.addressLines = addressLines;
  }


  @Override
  public String[] getFormatted() {
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


  @Override
  public Country getCountry() {
    return country;
  }

}
