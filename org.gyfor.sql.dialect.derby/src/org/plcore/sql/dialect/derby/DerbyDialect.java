package org.plcore.sql.dialect.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.osgi.service.component.annotations.Component;
import org.plcore.sql.dialect.IDialect;


/**
 * An implementation of IDialect for the Derby database.
 * 
 * @author Kevin Holloway
 * 
 */
@Component(property="name=DerbyEmbedded")
public class DerbyDialect implements IDialect {

  @Override
  public String getDriver () {
    return "org.apache.derby.jdbc.ClientDriver";
  }
  
  
  @Override
  public String getName () {
    return "Derby";
  }
  

  @Override
  public String getURLTemplate () {
    return "jdbc:postgresql://{0}/{1}";
  }


  @Override
  public Connection getConnection(String serverName, String dbName, Properties props) {
    String driverClassName = getDriver();
    try {
      Class.forName(driverClassName).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
     
    String urlTemplate = getURLTemplate();
    String url = MessageFormat.format(urlTemplate, serverName, dbName);

    Connection conn = null;
    try {
      conn = DriverManager.getConnection(url, props);
    } catch (SQLException ex) {
      System.out.println(url);
      System.out.println(props);
      throw new RuntimeException(ex);
    }
    return conn;
  }


  @Override
  public ConnectionFactory getConnectionFactory (String serverName, String dbName, Properties props) {
    String driverClassName = getDriver();
    try {
      Class.forName(driverClassName).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }

    String urlTemplate = getURLTemplate();
    String url = MessageFormat.format(urlTemplate, serverName, dbName);

    return new DriverManagerConnectionFactory(url, props);
  }

  
  @Override
  public Connection getConnection (Object connFactory) {
    try {
      ConnectionFactory cf = (ConnectionFactory)connFactory;
      return cf.createConnection();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
