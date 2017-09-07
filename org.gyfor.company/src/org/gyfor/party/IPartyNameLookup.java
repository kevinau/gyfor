package org.gyfor.party;

import java.io.IOException;

public interface IPartyNameLookup {

  /** 
   * Return the company that is identified by the company number, or
   * <code>null</code> if no company matches the company number.
   * <p>
   * A database or network service may be used to find the company. If
   * the database or network service cannot be accessed, an IOException
   * will be thrown.  Application code should deal with this situation.
   */
  public String getPartyName (String companyNumber) throws IOException;
  
}
