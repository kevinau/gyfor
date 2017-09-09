package org.gyfor.busnum;

import java.io.IOException;

public interface IBusinessLookup {
  
  /** 
   * Return the name of the business that is identified by the business number, or
   * <code>null</code> if no business matches the business number.
   * <p>
   * A database or network service may be used to find the business. If
   * the database or network service cannot be accessed, an IOException
   * will be thrown.  Application code should deal with this situation.
   */
  public String getBusinessName (String businessNumber) throws IOException;

}
