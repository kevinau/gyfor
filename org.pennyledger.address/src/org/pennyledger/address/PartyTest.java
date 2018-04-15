package org.pennyledger.address;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class PartyTest {
  
  @Reference
  private CountryType countryType;
  
  @Reference
  private Party party;
  
  
  @Activate
  protected void activate() {
    System.out.println("Party test................" + party);
    System.out.println("Party test................" + party.getAddress());
    System.out.println("Party test................" + party.getAddress().getClass());
    ((Address)party.getAddress()).setCountry(countryType.valueOf("AU"));
    System.out.println("Party test................" + ((Address)party.getAddress()).getCountry());
    System.out.println("Party test................" + ((Address)party.getAddress()).getCountryAddress());
    ((Address)party.getAddress()).setCountry(countryType.valueOf("NZ"));
    System.out.println("Party test................" + ((Address)party.getAddress()).getCountry());
    System.out.println("Party test................" + ((Address)party.getAddress()).getCountryAddress());
    ((Address)party.getAddress()).setCountry(countryType.valueOf("UK"));
    System.out.println("Party test................" + ((Address)party.getAddress()).getCountry());
    System.out.println("Party test................" + ((Address)party.getAddress()).getCountryAddress());
  }
}
