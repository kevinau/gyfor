package org.pennyledger.address;

import org.plcore.value.ICode;

public interface ILiveAddressProperties {
  
  public ICode defaultCountry();
  
  public int countryCount();
  
  public IAddress lookupAddress(ICode country);

}
