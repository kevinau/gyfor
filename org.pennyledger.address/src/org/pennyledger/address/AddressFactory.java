package org.pennyledger.address;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.gyfor.object.IEntityFactory;
import org.gyfor.osgi.ComponentConfiguration;
import org.gyfor.osgi.Configurable;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;


@Component(configurationPolicy=ConfigurationPolicy.OPTIONAL)
public class AddressFactory implements IEntityFactory<IAddress> {

  @Reference
  private volatile List<ICountryAddress> countryAddresses;
  
  @Configurable
  private String defaultCountry = Locale.getDefault().getCountry();
  
  
  @Activate
  public void activate (ComponentContext context) {
    ComponentConfiguration.load(this, context);
  }
  
  
  private ICountryAddress getAddress(String country) {
    for (ICountryAddress address : countryAddresses) {
      if (address.getCountry().equals(country)) {
        return address;
      }
    }
    throw new IllegalArgumentException("'" + country + "' is not a valid country");
  };
  
  
  @Override
  public IAddress newEntityInstance() {
    if (countryAddresses == null) {
      return new GenericAddress();
    } else {
      switch (countryAddresses.size()) {
      case 0 :
        return new GenericAddress();
      case 1 :
        return countryAddresses.get(0);
      default :
        return getAddress(defaultCountry);
      }
    }
  }

}
