package org.plcore.sql;

import org.plcore.sql.dialect.IDialect;

public interface IConnectionFactory {

  public IConnection getIConnection();
  
  public java.sql.Connection getConnection();
  
  public IDialect getDialect();
  
}
