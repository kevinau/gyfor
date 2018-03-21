package org.plcore.sql;

import java.sql.SQLException;

public class ResultSetMetaData implements IResultSetMetaData {

  private java.sql.ResultSetMetaData metadata;
  
  public ResultSetMetaData (java.sql.ResultSetMetaData metadata) {
    this.metadata = metadata;
  }

  @Override
  public int getColumnCount() {
    try {
      return metadata.getColumnCount();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String getColumnLabel(int i) {
    try {
      return metadata.getColumnLabel(i);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public String getColumnName(int i) {
    try {
      return metadata.getColumnName(i);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public int getColumnType(int i) {
    try {
      return metadata.getColumnType(i);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
}
