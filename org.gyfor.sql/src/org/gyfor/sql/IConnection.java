package org.gyfor.sql;



public interface IConnection extends AutoCloseable {

  public IPreparedStatement prepareStatement (String sql);
  
  @Override
  public void close ();

  public void setAutoCommit(boolean b);

  public void commit();

  public void rollback();

  public java.sql.Connection getUnderlyingConnection();
  
  public void executeCommand (String sql);
  
  public IDatabaseMetaData getMetaData();
  
}
