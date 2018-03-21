package org.plcore.sql;

import java.util.Properties;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;
import org.plcore.sql.dialect.IDialect;


@Component(configurationPolicy=ConfigurationPolicy.REQUIRE)
public class ConnectionFactory implements IConnectionFactory {

  @Reference(name="dialect", cardinality=ReferenceCardinality.MANDATORY)
  private IDialect dialect;
  
  @Configurable(required=true)
  private String serverName;
  
  @Configurable(required=true)
  private String dbName;
  
  @Configurable(required=true)
  private String userName;
  
  @Configurable(required=true)
  private String password;
  
  
  private Object connectionFactory;
  
  
  @Activate
  public void activate (ComponentContext componentContext) {
    ComponentConfiguration.load(this, componentContext);

//    String driver = dialect.getDriver();
//    if (driver != null) {
//      try {
//        Class.forName(driver).newInstance();
//      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
//        throw new RuntimeException(ex);
//      }
//    }
    
    Properties props2 = new Properties();
    if (userName != null) {
      props2.put("user", userName);
      props2.put("password", password);
    }
    connectionFactory = dialect.getConnectionFactory(serverName, dbName, props2);
  }
  
  
  @Deactivate
  void deactivate () {
  }
  
  
//  public IConnection getIConnection2() {
//    java.sql.Connection conn;
//    try {
//      conn = connectionFactory.createConnection();
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    }
//    return new Connection(conn);
//  }
  
  
  @Override
  public IConnection getIConnection() {
    java.sql.Connection conn = getConnection();
    return new org.plcore.sql.Connection(conn, dialect);
  }
  
  
//  public java.sql.Connection getConnection2() {
//    java.sql.Connection conn;
//    try {
//      conn = connectionFactory.createConnection();
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    }
//    return conn;
//  }
  
  
  @Override
  public java.sql.Connection getConnection() {
    Properties props2 = new Properties();
    if (userName != null) {
      props2.put("user", userName);
      props2.put("password", password);
    }
    //conn = dialect.getConnection(serverName, dbName, props2);
    return dialect.getConnection(connectionFactory);
  }

  
  @Override
  public IDialect getDialect () {
    return dialect;
  }
  
}
