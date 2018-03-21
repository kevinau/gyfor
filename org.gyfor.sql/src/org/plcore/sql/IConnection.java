package org.plcore.sql;

import org.plcore.sql.dialect.IDialect;

public interface IConnection extends AutoCloseable {

  public IPreparedStatement prepareStatement (String sql);
  
  public default IPreparedStatement prepareStatement (StringBuilder sql) {
    return prepareStatement(sql.toString());
  }
 
  
  @Override
  public void close ();

  public void setAutoCommit(boolean b);

  public void commit();

  public void rollback();

  public java.sql.Connection getUnderlyingConnection();
  
  public void executeCommand (String sql);
  
  public IDatabaseMetaData getMetaData();

  public IDialect getDialect();
  
}
