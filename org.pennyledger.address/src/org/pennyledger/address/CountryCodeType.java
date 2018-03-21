package org.pennyledger.address;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.type.IType;
import org.plcore.type.builtin.CodeType;
import org.plcore.value.ICode;


@Component(service = IType.class, property = "name=CountryCodeType")
public class CountryCodeType extends CodeType<ICode> {

  @Reference(policy = ReferencePolicy.DYNAMIC)
  private final List<ICountryAddress> addressStyles = new CopyOnWriteArrayList<>();

  
  @Override
  protected synchronized List<ICode> getValues () {
    List<ICode> values = new ArrayList<>(addressStyles.size());
    for (ICountryAddress address : addressStyles) {
      values.add(address.getCountry());
    }
    return values;
  }
  
}
