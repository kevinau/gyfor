package org.pennyledger.address;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;
import org.plcore.type.UserEntryException;
import org.plcore.userio.Embeddable;
import org.plcore.userio.FactoryFor;
import org.plcore.userio.Optional;


@Component
@Embeddable
public class Address implements IAddress {

  @Reference
  private CountryType countryType;
  
  @Reference(policy = ReferencePolicy.DYNAMIC)
  private final List<ILocalizedAddress> countryAddresses = new CopyOnWriteArrayList<>();


  @Activate
  private void activate (ComponentContext context) throws UserEntryException {
    // Set field default values
    country = countryType.getLocal();
    
    // Override field defaults with configurable values, if supplied
    ComponentConfiguration.load(this, context);
  }
  
  
  @Optional
  @Configurable(name = "defaultCountry")
  private Country country;
  

  private ILocalizedAddress countryAddress;
  
  
  @FactoryFor("countryAddress")
  private ILocalizedAddress getAddressInstance() {
    for (ILocalizedAddress address : countryAddresses) {
      if (address.getCountry().equals(country)) {
        return address;
      }
    }
    return new GenericAddress();
  }
  
  
  @Override
  public String[] getFormatted() {
    String[] formatted;
    if (countryAddress == null) {
      formatted = new String[0];
    } else {
      formatted = countryAddress.getFormatted();
    }
    
    Country localCountry = countryType.getLocal();
    if (!country.equals(localCountry)) {
      int n = formatted.length;
      formatted = Arrays.copyOf(formatted, n + 1);
      formatted[n] = country.getDescription().toUpperCase();
    }
    return formatted;
  }

  
  public void setCountry (Country country) {
    this.country = country;
  }
  
  
  public Country getCountry() {
    return country;
  }
  
  public ILocalizedAddress getCountryAddress() {
    return countryAddress;
  }
  
}
