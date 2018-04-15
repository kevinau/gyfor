package org.pennyledger.address;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.type.IType;
import org.plcore.type.builtin.CodeType;


@Component(service = {IType.class, CountryType.class})
public class CountryType extends CodeType<Country> {

  @Reference(policy = ReferencePolicy.DYNAMIC)
  private final List<ILocalizedAddress> countryAddresses = new CopyOnWriteArrayList<>();

  
  @Override
  protected List<Country> getValues () {
    List<Country> values = new ArrayList<>(countryAddresses.size());
    for (ILocalizedAddress address : countryAddresses) {
      values.add(address.getCountry());
    }
    return values;
  }
  
  
  public Country getLocal() {
    String localCode = Locale.getDefault().getCountry();
    return valueOf(localCode);
  }
  
}
