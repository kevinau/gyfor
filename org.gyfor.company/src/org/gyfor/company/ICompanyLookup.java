package org.gyfor.company;

import java.io.IOException;

public interface ICompanyLookup {

  /** 
   * Return the company that is identified by the company number, or
   * <code>null</code> if not.
   * <p>
   * A database or network service may be used to find the company. If
   * the database or network service cannot be accessed, an IOException
   * will be thrown.  Application code should deal with this situation.
   */
  public Company getCompany (String companyNumber) throws IOException;
  
}
