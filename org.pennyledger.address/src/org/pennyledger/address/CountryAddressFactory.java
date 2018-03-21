package org.pennyledger.address;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.osgi.Configurable;
import org.plcore.userio.IEntityFactory;
import org.plcore.value.ICode;


@Component(configurationPolicy=ConfigurationPolicy.OPTIONAL)
public class CountryAddressFactory implements IEntityFactory<IAddress>, ILiveAddressProperties {

  @Reference(policy = ReferencePolicy.DYNAMIC)
  private final List<ICountryAddress> addressStyles = new CopyOnWriteArrayList<>();
  
  @Configurable
  private String defaultCountryCode = Locale.getDefault().getCountry();
  
  
  @Override
  public ICode defaultCountry() {
    ICode first = null;
    for (ICountryAddress address : addressStyles) {
      if (address.getCountry().getCode().equals(defaultCountryCode)) {
        return address.getCountry();
      }
      if (first == null) {
        first = address.getCountry();
      }
    }
    return first;
  }
  
  
  @Override
  public int countryCount() {
    return addressStyles.size();
  }

  
  @Override
  public IAddress lookupAddress(ICode country) {
    for (ICountryAddress addressStyle : addressStyles) {
      if (addressStyle.getCountry().equals(country)) {
        IAddress address;
        try {
          address = addressStyle.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
          throw new RuntimeException(ex);
        } 
        return address;
      }
    }
    return new GenericAddress();
  }
  

  @Override
  public ICountryAddress newEntityInstance() {
    return new CountryAddress(this);
  }
}
