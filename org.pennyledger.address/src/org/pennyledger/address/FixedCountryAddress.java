package org.pennyledger.address;

import org.gyfor.object.NotItemField;

public class FixedCountryAddress implements IAddress {

  private ICountryAddress countryAddress;

  
  public FixedCountryAddress (ICountryAddress countryAddress) {
    this.countryAddress = countryAddress;
  }
  
  
 public ICountryAddress getCountryAddress() {
    return countryAddress;
  }

  
  public void setCountryAddress(ICountryAddress countryAddress) {
    this.countryAddress = countryAddress;
  }


  @Override
  @NotItemField
  public String[] getAddressLines() {
    return countryAddress.getAddressLines();
  }
  
}
