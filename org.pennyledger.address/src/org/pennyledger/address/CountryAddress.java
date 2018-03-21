package org.pennyledger.address;

import org.plcore.userio.DefaultFor;
import org.plcore.userio.FactoryFor;
import org.plcore.userio.IOField;
import org.plcore.userio.Optional;
import org.plcore.value.ICode;


public class CountryAddress implements ICountryAddress {

  private final ILiveAddressProperties addressProps;
  
  
  public CountryAddress(ILiveAddressProperties addressProps) {
    this.addressProps = addressProps;
  }
 
  
  @IOField(length = 2, pattern = "^[A-Z][A-Z]$", requiredMessage = "Required", 
                                                    errorMessage = "Not a two letter code",
                                                    type = "CountryCodeType")
  @Optional
  private ICode country;
  
  @IOField
  private IAddress address;
  
  
  @DefaultFor("country")
  private ICode getDefaultCountry() {
    return addressProps.defaultCountry();
  }
  
  
  @FactoryFor("address")
  private IAddress getAddressInstance() {
    IAddress addressStyle = addressProps.lookupAddress(country);
    if (addressStyle != null) {
      IAddress address;
      try {
        address = addressStyle.getClass().newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
      return address;
    } else {
      return new GenericAddress();
    }
  }


  @Override
  public String[] getFormatted(ICode localCountry) {
    return address.getFormatted(localCountry);
  }


  @Override
  public ICode getCountry() {
    return country;
  }
  
}
