package org.pennyledger.address;

import org.plcore.value.Code;

public class Country extends Code<Country> {

  private static final long serialVersionUID = 1L;

  private static volatile CountryType factory = new CountryType();
  
  public Country(String code, String description) {
    super(code, description);
  }

  public static Country valueOf (String code) {
    return factory.valueOf(code);
  }
  
}
