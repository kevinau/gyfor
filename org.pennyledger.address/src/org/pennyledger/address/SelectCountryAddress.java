package org.pennyledger.address;

import java.util.Locale;
import java.util.function.Function;

import org.gyfor.object.FactoryFor;
import org.gyfor.object.ItemField;
import org.gyfor.object.Optional;

public class SelectCountryAddress implements IAddress {

  private transient Function<String, ICountryAddress> addressMapping;
  
  private String country;
  
  private ICountryAddress countryAddress;

  
  public SelectCountryAddress (String defaultCountry, Function<String, ICountryAddress> addressMapping) {
    if (defaultCountry != null) {
      this.country = defaultCountry;
    } else {
      Locale locale = Locale.getDefault();
      this.country = locale.getCountry();
    }
    this.addressMapping = addressMapping;
  }
  
  
  @FactoryFor("countryAddress")
  private ICountryAddress addressFactory() {
    if (country == null) {
      return null;
    } else {
      return addressMapping.apply(country);
    }
  }
  
  
  public String getCountry() {
    return country;
  }

  
  @ItemField(length = 2, pattern = "^[A-Z][A-Z]$", requiredMessage = "Two letter country code required", 
                                                      errorMessage = "Not a two letter country code")
  @Optional
  public void setCountry(String country) {
    this.country = country;
  }

  
  public ICountryAddress getCountryAddress() {
    return countryAddress;
  }

  
  public void setCountryAddress(ICountryAddress countryAddress) {
    this.countryAddress = countryAddress;
  }


  @Override
  public String[] getAddressLines() {
    return countryAddress.getAddressLines();
  }
  
}
