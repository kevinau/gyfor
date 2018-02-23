package org.pennyledger.address;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import org.gyfor.object.CodeSource;
import org.gyfor.object.FactoryFor;
import org.gyfor.object.IOField;
import org.gyfor.object.Optional;
import org.gyfor.value.ICode;

public class SelectCountryAddress implements IAddress {

  private transient Function<String, ICountryAddress> addressMapping;
  
  @IOField
  @CodeSource(???????)
  private ICode country;
  
  @IOField
  private ICountryAddress countryAddress;

  
  public SelectCountryAddress (String defaultCountry, Supplier<List<String>> addressCountries, Function<String, ICountryAddress> addressMapping) {
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

  
  @IOField(length = 2, pattern = "^[A-Z][A-Z]$", requiredMessage = "Two letter country code required", 
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
