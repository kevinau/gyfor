package org.gyfor.sql;

import org.gyfor.sql.dialect.IDialect;

public interface IConnectionFactory {

  public IConnection getIConnection();
  
  public java.sql.Connection getConnection();
  
  public IDialect getDialect();
  
}
