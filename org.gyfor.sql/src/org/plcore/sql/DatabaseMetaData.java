package org.plcore.sql;

import java.sql.SQLException;

public class DatabaseMetaData implements IDatabaseMetaData {

  private java.sql.DatabaseMetaData metadata;
    
  public DatabaseMetaData (java.sql.DatabaseMetaData metadata) {
    this.metadata = metadata;
  }

  
  @Override
  public IResultSet getTables (String schema, String tableName, String[] types) {
    try {
      return new ResultSet(metadata.getTables(null, schema, tableName, types));
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public IResultSet getColumns (String schema, String tableName, String columnName) {
    try {
      return new ResultSet(metadata.getColumns(null, schema, tableName, columnName));
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
