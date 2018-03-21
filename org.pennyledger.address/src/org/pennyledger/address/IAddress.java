package org.pennyledger.address;

import org.plcore.value.ICode;

public interface IAddress {

  public String[] getFormatted(ICode localCountry);

}
