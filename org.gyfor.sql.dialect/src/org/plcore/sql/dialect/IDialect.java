/*******************************************************************************
 * Copyright (c) 2008 Kevin Holloway (kholloway@geckosoftware.com.au).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 ******************************************************************************/
package org.plcore.sql.dialect;

import java.sql.Connection;
import java.util.Properties;

/**
 * An SQL dialect (such as Postgresql, Derby, etc). Classes that implement this
 * interface provide enough information to create a database connection.
 * 
 * @author Kevin Holloway
 * 
 */
public interface IDialect {

  /**
   * The name of this SQL dialect.
   */
  public String getName();


  /**
   * The name of the driver required for this SQL dialect.  If not required, this
   * method should return null.
   */
  public default String getDriver() {
    return null;
  }

  
  /**
   * A MessageFormat string that allows a URL to be created using a server and
   * database name.
   */
  public String getURLTemplate();

  /**
   * A method that returns a database connection given a URL and a set of
   * properties. The properties will include username and password.
   * 
   * @param url
   *          - a String that contains a URL. The URL will have been obtained
   *          from the getURLTemplate or provided directly.
   * @param props
   *          - a Properties object that contains a username and password.
   * @return a database Connection.
   */
  public Connection getConnection (String serverName, String dbName, Properties props);


  public Object getConnectionFactory (String serverName, String dbName, Properties props);
  
  
  public Connection getConnection (Object connectionFactory);
  
  
  public default String dropTableTemplate() {
    return "DROP TABLE {0};";
  }
  
  
  public default String noTableState() {
    return "42X05";
  }
  
}
