package org.pennyledger.address;

import java.util.Locale;
import java.util.function.Function;

import org.gyfor.object.DefaultFor;
import org.gyfor.object.EntryMode;
import org.gyfor.object.FactoryFor;
import org.gyfor.object.ItemField;
import org.gyfor.object.ModeFor;
import org.gyfor.object.Optional;

public class Address {

  private transient String setCountry;
  
  private transient String defaultCountry;
  
  private transient Function<String, ICountryAddress> addressMapping;
  
  private String country;
  
  private ICountryAddress countryAddress;

  
  public Address (String setCountry, String defaultCountry, Function<String, ICountryAddress> addressMapping) {
    this.setCountry = setCountry;
    this.defaultCountry = defaultCountry;
    this.addressMapping = addressMapping;
  }
  
  
  @ModeFor("country")
  private EntryMode countryEntryMode() {
    if (setCountry != null) {
      return EntryMode.HIDDEN;
    } else {
      return EntryMode.ENABLED;
    }
  }
  
  
  @DefaultFor("country")
  private String countryDefault() {
    if (setCountry == null) {
      if (defaultCountry != null) {
        return defaultCountry;
      } else {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
      }
    } else {
      return null;
    }
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
  
}
