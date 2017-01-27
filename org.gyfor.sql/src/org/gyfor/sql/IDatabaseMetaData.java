package org.gyfor.sql;

public interface IDatabaseMetaData {

  public IResultSet getTables(String schema, String tableName, String[] types);

  public IResultSet getColumns(String schema, String tableName, String columnName);

}
